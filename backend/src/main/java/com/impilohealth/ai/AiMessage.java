package com.impilohealth.ai;

/** One turn of conversation. role is "user" or "assistant". */
public record AiMessage(String role, String content) {
}
