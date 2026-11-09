package com.impilohealth.ai;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

    List<ChatLog> findTop10ByUserIdOrderByIdDesc(Long userId);
}
