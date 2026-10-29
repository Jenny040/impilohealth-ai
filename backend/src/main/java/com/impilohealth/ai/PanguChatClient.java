package com.impilohealth.ai;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Huawei Cloud Pangu NLP (ModelArts Studio) chat client.
 *
 * Assumes the OpenAI-style chat-completions request/response shape that ModelArts Studio exposes for
 * deployed Pangu models. The endpoint, model name and auth header are all configuration, so if your
 * deployment's API differs, only this class needs to change. Verify against your own Huawei Cloud
 * console (ModelArts Studio -> your deployed model -> API call).
 */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "pangu")
public class PanguChatClient implements HealthChatClient {

    private final RestClient http;
    private final AiProperties.Pangu cfg;

    public PanguChatClient(AiProperties props, RestClient.Builder builder) {
        if (props.pangu() == null || props.pangu().endpoint() == null || props.pangu().apiKey() == null) {
            throw new IllegalStateException(
                    "app.ai.provider=pangu requires app.ai.pangu.endpoint and app.ai.pangu.api-key");
        }
        this.cfg = props.pangu();
        this.http = builder.build();
    }

    @Override
    public String name() {
        return "huawei-pangu";
    }

    @Override
    public String chat(String systemPrompt, List<AiMessage> history, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        history.forEach(m -> messages.add(Map.of("role", m.role(), "content", m.content())));
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> body = Map.of(
                "model", cfg.model() == null ? "pangu" : cfg.model(),
                "messages", messages,
                "temperature", 0.4);

        String header = cfg.authHeader() == null ? "Authorization" : cfg.authHeader();
        String prefix = cfg.authPrefix() == null ? "Bearer " : cfg.authPrefix();

        try {
            JsonNode root = http.post()
                    .uri(cfg.endpoint())
                    .header(header, prefix + cfg.apiKey())
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
            String text = root == null ? "" : root.path("choices").path(0).path("message").path("content").asText("");
            if (text.isBlank()) {
                throw new AiUnavailableException("Pangu returned an empty response");
            }
            return text;
        } catch (RestClientException e) {
            throw new AiUnavailableException("Pangu request failed", e);
        }
    }
}
