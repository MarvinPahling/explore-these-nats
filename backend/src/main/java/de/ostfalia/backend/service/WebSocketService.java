package de.ostfalia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ostfalia.backend.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        log.info("WebSocket session registered: sessionId={}, totalSessions={}", session.getId(), sessions.size());
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId());
        log.info("WebSocket session unregistered: sessionId={}, totalSessions={}", session.getId(), sessions.size());
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastMessage() {
        Message message = new Message(random.nextInt(1000));
        int activeSessionCount = 0;

        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(jsonMessage));
                    activeSessionCount++;
                    log.debug("Sent message to sessionId={}: {}", session.getId(), jsonMessage);
                } catch (IOException e) {
                    log.error("Error sending message to sessionId={}: {}", session.getId(), e.getMessage(), e);
                }
            }
        }

        if (activeSessionCount > 0) {
            log.debug("Broadcast message to {} active sessions", activeSessionCount);
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
