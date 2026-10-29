package com.impilohealth.ai;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** Development fallback while Pangu access is being set up. Not for production. */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "gemini")
public class GeminiChatClient implements HealthChatClient {

    private final RestClient http;
    private final AiProperties.Gemini cfg;

    public GeminiChatClient(AiProperties props, RestClient.Builder builder) {
        if (props.gemini() == null || props.gemini().apiKey() == null) {
            throw new IllegalStateException("app.ai.provider=gemini requires app.ai.gemini.api-key");
        }
        this.cfg = props.gemini();
        this.http = builder.baseUrl("https://generativelanguage.googleapis.com").build();
    }

    @Override
    public String name() {
        return "gemini";
    }

    @Override
    public String chat(String systemPrompt, List<AiMessage> history, String userMessage) {
        List<Map<String, Object>> contents = new ArrayList<>();
        history.forEach(m -> contents.add(turn("assistant".equals(m.role()) ? "model" : "user", m.content())));
        contents.add(turn("user", userMessage));

        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", systemPrompt))),
                "contents", contents);

        String model = cfg.model() == null ? "gemini-1.5-flash" : cfg.model();
        try {
            JsonNode root = http.post()
                    .uri("/v1beta/models/{model}:generateContent", model)
                    .header("x-goog-api-key", cfg.apiKey())
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            String text = root == null ? ""
                    : root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
            if (text.isBlank()) {
                throw new AiUnavailableException("Gemini returned an empty response");
            }
            return text;
        } catch (RestClientException e) {
            throw new AiUnavailableException("Gemini request failed", e);
        }
    }

    private static Map<String, Object> turn(String role, String text) {
        return Map.of("role", role, "parts", List.of(Map.of("text", text)));
    }
}
