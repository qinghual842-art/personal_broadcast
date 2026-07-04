package com.blog.module.agent.provider;

import com.blog.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LlmProviderFactory {

    private final Map<String, LlmProvider> providers;
    private final GenericOpenAiProvider genericProvider;

    public LlmProviderFactory(List<LlmProvider> providerList, GenericOpenAiProvider genericProvider) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(LlmProvider::getName, Function.identity()));
        this.genericProvider = genericProvider;
    }

    public LlmProvider getProvider(String name) {
        LlmProvider provider = providers.get(name);
        if (provider != null) {
            return provider;
        }
        // Fallback: treat unknown provider as generic OpenAI-compatible
        return genericProvider;
    }
}
