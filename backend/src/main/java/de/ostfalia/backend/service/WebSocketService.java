package de.ostfalia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ostfalia.backend.domain.Message;
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

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket session registered: " + session.getId());
    }

    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId());
        System.out.println("WebSocket session unregistered: " + session.getId());
    }

    @Scheduled(fixedRate = 1000)
    public void broadcastMessage() {
        Message message = new Message(random.nextInt(1000));

        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    session.sendMessage(new TextMessage(jsonMessage));
                    System.out.println("Sent message to " + session.getId() + ": " + jsonMessage);
                } catch (IOException e) {
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        });
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
