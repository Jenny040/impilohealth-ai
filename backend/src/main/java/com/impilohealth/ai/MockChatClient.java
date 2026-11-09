package com.impilohealth.ai;

import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Offline default: lets the frontend and tests run without any cloud credentials. */
@Component
@ConditionalOnProperty(name = "app.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockChatClient implements HealthChatClient {

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public String chat(String systemPrompt, List<AiMessage> history, String userMessage) {
        return "[mock AI] I hear you. Rest, drink water, and see a clinic if this continues. You said: " + userMessage;
    }
}
