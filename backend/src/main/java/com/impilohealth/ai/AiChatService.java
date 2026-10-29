package com.impilohealth.ai;

import com.impilohealth.audit.AuditService;
import com.impilohealth.user.User;
import com.impilohealth.user.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    public record ChatResult(String reply, String provider) {
    }

    private final HealthChatClient client;
    private final AiProperties props;
    private final UserRepository users;
    private final ChatLogRepository chatLogs;
    private final AuditService audit;

    public AiChatService(HealthChatClient client, AiProperties props, UserRepository users,
                         ChatLogRepository chatLogs, AuditService audit) {
        this.client = client;
        this.props = props;
        this.users = users;
        this.chatLogs = chatLogs;
        this.audit = audit;
    }

    public ChatResult chat(String email, String message) {
        User user = users.findByEmailIgnoreCase(email).orElseThrow();

        List<ChatLog> recent = new ArrayList<>(chatLogs.findTop10ByUserIdOrderByIdDesc(user.getId()));
        Collections.reverse(recent);
        List<AiMessage> history = recent.stream().map(l -> new AiMessage(l.getRole(), l.getContent())).toList();

        String reply = client.chat(props.systemPrompt(), history, message);

        chatLogs.save(new ChatLog(user.getId(), "user", message));
        chatLogs.save(new ChatLog(user.getId(), "assistant", reply.length() > 4000 ? reply.substring(0, 4000) : reply));
        audit.record(email, "AI_CHAT", "chat_logs");

        return new ChatResult(reply, client.name());
    }
}
