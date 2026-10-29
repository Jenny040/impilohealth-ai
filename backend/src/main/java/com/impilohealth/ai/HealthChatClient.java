package com.impilohealth.ai;

import java.util.List;

/**
 * Provider-agnostic chat model. The Huawei Pangu implementation is the production one;
 * the rest of the backend only depends on this interface.
 */
public interface HealthChatClient {

    String name();

    String chat(String systemPrompt, List<AiMessage> history, String userMessage);
}
