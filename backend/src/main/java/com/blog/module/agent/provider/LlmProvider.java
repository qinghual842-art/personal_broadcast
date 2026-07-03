package com.blog.module.agent.provider;

import java.util.List;

public interface LlmProvider {
    String getName();
    String chat(ChatRequest request);
    boolean validateConfig(String apiKey);

    record ChatRequest(
            String modelName,
            List<Message> messages,
            double temperature,
            int maxTokens,
            String apiKey
    ) {}

    record Message(String role, String content) {}

    record ChatResponse(String content, String modelName, int tokensUsed) {}
}
