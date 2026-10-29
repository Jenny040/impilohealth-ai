package com.impilohealth.ai;

import com.impilohealth.ai.AiChatService.ChatResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    public record ChatRequest(@NotBlank @Size(max = 2000) String message) {
    }

    private final AiChatService service;

    public AiChatController(AiChatService service) {
        this.service = service;
    }

    @PostMapping("/chat")
    public ChatResult chat(@Valid @RequestBody ChatRequest request, Authentication authentication) {
        return service.chat(authentication.getName(), request.message());
    }
}
