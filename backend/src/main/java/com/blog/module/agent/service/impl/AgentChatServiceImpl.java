package com.blog.module.agent.service.impl;

import com.alibaba.fastjson2.JSON;
import com.blog.exception.BusinessException;
import com.blog.module.agent.entity.Agent;
import com.blog.module.agent.entity.AgentConversation;
import com.blog.module.agent.mapper.AgentConversationMapper;
import com.blog.module.agent.mapper.AgentMapper;
import com.blog.module.agent.provider.LlmProvider;
import com.blog.module.agent.provider.LlmProviderFactory;
import com.blog.module.agent.service.AgentChatService;
import com.blog.module.agent.vo.AgentChatVO;
import com.blog.module.agent.vo.AgentMessageVO;
import com.blog.util.AesUtil;
import com.blog.util.IpUtil;
import com.blog.util.RedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentChatServiceImpl implements AgentChatService {

    private final AgentMapper agentMapper;
    private final AgentConversationMapper conversationMapper;
    private final LlmProviderFactory providerFactory;
    private final AesUtil aesUtil;
    private final StringRedisTemplate redisTemplate;

    private static final Set<String> SENSITIVE_WORDS = Set.of("敏感词1", "敏感词2", "违规内容");
    private static final int SENSITIVE_HIT_LIMIT = 5;
    private static final int SENSITIVE_WINDOW_MINUTES = 10;
    private static final int RATE_LIMIT_WINDOW_SECONDS = 60;
    private static final int SESSION_TTL_HOURS = 24;
    private static final int DEFAULT_MAX_MESSAGE_LENGTH = 5000;
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String HISTORY_SEPARATOR = "||";

    @Override
    public AgentChatVO chat(Long agentId, String sessionId, String message, HttpServletRequest request) {
        requireNonNull(agentId, "智能体ID不能为空");
        requireNotBlank(message, "消息内容不能为空");
        requireNonNull(request, "请求对象不能为空");

        if (message.length() > DEFAULT_MAX_MESSAGE_LENGTH) {
            throw new BusinessException(400, "消息内容过长，最多允许" + DEFAULT_MAX_MESSAGE_LENGTH + "个字符");
        }

        Agent agent = agentMapper.selectById(agentId);
        if (agent == null || agent.getIsEnabled() == null || agent.getIsEnabled() == 0) {
            throw new BusinessException(400, "智能体不存在或已关闭");
        }

        String ip = IpUtil.getClientIp(request);

        // Rate limit check
        checkRateLimit(agentId, ip, agent.getRateLimitPerMin());

        // Sensitive word check
        checkSensitiveWords(message, ip);

        // Generate session ID if not provided
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(
                    RedisKeyUtil.agentSessionKey(sessionId), String.valueOf(agentId),
                    Duration.ofHours(SESSION_TTL_HOURS));
        }

        // Build messages for LLM
        List<LlmProvider.Message> llmMessages = new ArrayList<>();
        llmMessages.add(new LlmProvider.Message("system",
                agent.getCapabilityDesc() != null && !agent.getCapabilityDesc().isBlank()
                        ? agent.getCapabilityDesc() : "你是一个有帮助的AI助手"));

        // Load conversation history from Redis
        String convKey = RedisKeyUtil.agentConversationKey(agentId, sessionId);
        Long historySize = redisTemplate.opsForList().size(convKey);
        List<String> historyStr;
        int maxMsgs = agent.getContextLength() != null ? agent.getContextLength() * 2 : 20;

        if (historySize != null && historySize > 0) {
            int start = Math.max(0, historySize.intValue() - maxMsgs);
            historyStr = redisTemplate.opsForList().range(convKey, start, -1);
        } else {
            historyStr = List.of();
        }

        if (historyStr != null && !historyStr.isEmpty()) {
            for (String entry : historyStr) {
                LlmProvider.Message msg = parseHistoryToLlmMessage(entry);
                if (msg != null) {
                    llmMessages.add(msg);
                }
            }
        }

        llmMessages.add(new LlmProvider.Message("user", message));

        // Decrypt API key and call LLM
        String apiKey = decryptApiKey(agent);
        LlmProvider provider = providerFactory.getProvider(agent.getProvider());

        LlmProvider.ChatRequest chatRequest = new LlmProvider.ChatRequest(
                agent.getModelName() != null ? agent.getModelName() : "gpt-3.5-turbo",
                llmMessages,
                agent.getTemperature() != null ? agent.getTemperature().doubleValue() : 0.7,
                agent.getMaxTokens() != null ? agent.getMaxTokens() : 2048,
                apiKey,
                agent.getBaseUrl()
        );

        String reply;
        try {
            reply = provider.chat(chatRequest);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Agent chat error: agentId={}, provider={}", agentId, agent.getProvider(), e);
            throw new BusinessException(500, "AI服务暂时不可用，请稍后重试");
        }

        // Save to Redis history
        String now = LocalDateTime.now().format(TIMESTAMP_FMT);
        String userMsg = formatHistoryEntry("user", message, now);
        String assistantMsg = formatHistoryEntry("assistant", reply, now);
        redisTemplate.opsForList().rightPushAll(convKey, userMsg, assistantMsg);
        redisTemplate.expire(convKey, Duration.ofHours(SESSION_TTL_HOURS));

        // Trim to context window
        if (historySize != null) {
            long newSize = historySize + 2;
            if (newSize > maxMsgs) {
                redisTemplate.opsForList().trim(convKey, newSize - maxMsgs, -1);
            }
        }

        // Async persist to DB
        persistConversation(agentId, sessionId, "user", message, ip);
        persistConversation(agentId, sessionId, "assistant", reply, ip);

        // Build response with history
        List<AgentMessageVO> history = parseHistoryToVOList(historyStr);

        AgentChatVO chatVO = new AgentChatVO();
        chatVO.setSessionId(sessionId);
        chatVO.setReply(reply);
        chatVO.setHistory(history);
        return chatVO;
    }

    @Override
    public List<AgentMessageVO> getHistory(Long agentId, String sessionId) {
        requireNonNull(agentId, "智能体ID不能为空");
        requireNotBlank(sessionId, "会话ID不能为空");

        String convKey = RedisKeyUtil.agentConversationKey(agentId, sessionId);
        List<String> historyStr = redisTemplate.opsForList().range(convKey, 0, -1);
        return parseHistoryToVOList(historyStr);
    }

    @Override
    public void clearHistory(Long agentId, String sessionId) {
        requireNonNull(agentId, "智能体ID不能为空");
        requireNotBlank(sessionId, "会话ID不能为空");

        redisTemplate.delete(RedisKeyUtil.agentConversationKey(agentId, sessionId));
        redisTemplate.delete(RedisKeyUtil.agentSessionKey(sessionId));
    }

    // ─── Private helpers ────────────────────────────────────────

    private void checkRateLimit(Long agentId, String ip, Integer limit) {
        String rateKey = RedisKeyUtil.agentRateLimitKey(agentId, ip);
        Long count = redisTemplate.opsForValue().increment(rateKey);
        if (count != null && count == 1) {
            redisTemplate.expire(rateKey, Duration.ofSeconds(RATE_LIMIT_WINDOW_SECONDS));
        }
        int maxPerMin = limit != null ? limit : 10;
        if (count != null && count > maxPerMin) {
            throw new BusinessException(429, "请求过于频繁，请稍后再试");
        }
    }

    private void checkSensitiveWords(String message, String ip) {
        for (String word : SENSITIVE_WORDS) {
            if (message.contains(word)) {
                String hitKey = RedisKeyUtil.sensitiveHitKey(ip);
                Long hits = redisTemplate.opsForValue().increment(hitKey);
                if (hits != null && hits == 1) {
                    redisTemplate.expire(hitKey, Duration.ofMinutes(SENSITIVE_WINDOW_MINUTES));
                }
                if (hits != null && hits > SENSITIVE_HIT_LIMIT) {
                    throw new BusinessException(400, "检测到违规内容，您已被临时限制使用");
                }
                throw new BusinessException(400, "消息包含不当内容，请修改后重试");
            }
        }
    }

    /**
     * Parse a history entry into an LLM message.
     * Format: "role||content||timestamp"
     */
    private LlmProvider.Message parseHistoryToLlmMessage(String entry) {
        if (entry == null || entry.isBlank()) return null;
        try {
            String[] parts = entry.split("\\|\\|", 3);
            if (parts.length >= 2) {
                return new LlmProvider.Message(parts[0], parts[1]);
            }
        } catch (Exception e) {
            log.warn("Failed to parse history entry for LLM: {}", entry.substring(0, Math.min(50, entry.length())), e);
        }
        return null;
    }

    /**
     * Parse history entries into AgentMessageVO list.
     * Format: "role||content||timestamp"
     */
    private List<AgentMessageVO> parseHistoryToVOList(List<String> historyStr) {
        if (historyStr == null || historyStr.isEmpty()) return List.of();

        List<AgentMessageVO> result = new ArrayList<>();
        for (String s : historyStr) {
            if (s == null) continue;
            try {
                String[] parts = s.split("\\|\\|", 3);
                if (parts.length >= 2) {
                    AgentMessageVO msg = new AgentMessageVO();
                    msg.setRole(parts[0]);
                    msg.setContent(parts[1]);
                    msg.setTimestamp(parts.length >= 3 ? parts[2] : "");
                    result.add(msg);
                }
            } catch (Exception e) {
                log.warn("Failed to parse history entry for VO: {}", s.substring(0, Math.min(50, s.length())), e);
            }
        }
        return result;
    }

    private String formatHistoryEntry(String role, String content, String timestamp) {
        return role + HISTORY_SEPARATOR + content + HISTORY_SEPARATOR + timestamp;
    }

    private String decryptApiKey(Agent agent) {
        if (agent.getApiKeyEncrypted() == null || agent.getApiKeyEncrypted().isBlank()) {
            throw new BusinessException(500, "智能体API Key未配置");
        }
        return aesUtil.decrypt(agent.getApiKeyEncrypted());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistConversation(Long agentId, String sessionId, String role, String content, String ip) {
        try {
            AgentConversation conv = new AgentConversation();
            conv.setAgentId(agentId);
            conv.setSessionId(sessionId);
            conv.setRole(role);
            conv.setContent(content);
            conv.setIpAddress(ip);
            conversationMapper.insert(conv);
        } catch (Exception e) {
            log.warn("Failed to persist conversation: agentId={}, sessionId={}, role={}", agentId, sessionId, role, e);
        }
    }

    // ─── Validation helpers ─────────────────────────────────────
    private void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new BusinessException(400, message);
        }
    }

    private void requireNotBlank(String str, String message) {
        if (str == null || str.isBlank()) {
            throw new BusinessException(400, message);
        }
    }
}
