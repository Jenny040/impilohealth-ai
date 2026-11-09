package com.impilohealth.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI configuration. provider = pangu (Huawei, production) | gemini (dev fallback) | mock (offline/tests).
 */
@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(String provider, String systemPrompt, Pangu pangu, Gemini gemini) {

    static final String DEFAULT_SYSTEM_PROMPT = """
            You are ImpiloHealth-AI, a caring, culturally aware health companion for people in Africa.
            Reply in the language the user writes in. Be concise and practical.
            You cover personal health, women's health (cycle), nutrition and mental wellness.
            You are NOT a doctor: never diagnose, and always recommend a clinic or doctor for serious or
            persistent symptoms. If the user mentions an emergency or self-harm, tell them to contact
            emergency services or a crisis line immediately.
            """;

    public AiProperties {
        if (systemPrompt == null || systemPrompt.isBlank()) {
            systemPrompt = DEFAULT_SYSTEM_PROMPT;
        }
    }

    /** Huawei Cloud Pangu (ModelArts Studio) chat endpoint. */
    public record Pangu(String endpoint, String apiKey, String model, String authHeader, String authPrefix) {
    }

    public record Gemini(String apiKey, String model) {
    }
}
