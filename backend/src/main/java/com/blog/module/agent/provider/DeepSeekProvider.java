package com.blog.module.agent.provider;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.blog.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeepSeekProvider implements LlmProvider {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    public String getName() {
        return "deepseek";
    }

    @Override
    public String chat(ChatRequest request) {
        List<Map<String, String>> messages = request.messages().stream()
                .map(m -> Map.of("role", m.role(), "content", m.content()))
                .toList();

        Map<String, Object> body = Map.of(
                "model", request.modelName(),
                "messages", messages,
                "temperature", request.temperature(),
                "max_tokens", request.maxTokens()
        );

        okhttp3.Request httpRequest = new okhttp3.Request.Builder()
                .url("https://api.deepseek.com/v1/chat/completions")
                .header("Authorization", "Bearer " + request.apiKey())
                .header("Content-Type", "application/json")
                .post(RequestBody.create(JSON.toJSONString(body), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                log.error("DeepSeek API error: status={}, body={}", response.code(), responseBody);
                throw new BusinessException(500, buildErrorMsg("DeepSeek", response.code(), responseBody));
            }
            JSONObject json = JSON.parseObject(responseBody);
            return json.getJSONArray("choices").getJSONObject(0)
                    .getJSONObject("message").getString("content");
        } catch (IOException e) {
            log.error("DeepSeek API call failed", e);
            throw new BusinessException(500, "AI服务连接失败，请稍后重试");
        }
    }

    private String buildErrorMsg(String provider, int status, String body) {
        String detail = body.length() > 300 ? body.substring(0, 300) + "..." : body;
        return "[" + provider + "] HTTP " + status + ": " + detail;
    }

    @Override
    public boolean validateConfig(String apiKey) {
        return apiKey != null && !apiKey.isBlank();
    }
}
