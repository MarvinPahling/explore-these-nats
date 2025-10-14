package de.ostfalia.backend.service;

import de.ostfalia.backend.domain.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService extends AbstractConnectionService {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * Register a new WebSocket session
     * @param session The WebSocket session to register
     */
    public void registerSession(WebSocketSession session) {
        sessions.put(session.getId(), session);
        System.out.println("WebSocket session registered: " + session.getId());
    }

    /**
     * Unregister a WebSocket session
     * @param session The WebSocket session to unregister
     */
    public void unregisterSession(WebSocketSession session) {
        sessions.remove(session.getId());
        System.out.println("WebSocket session unregistered: " + session.getId());
    }

    @Override
    public void sendMessage(Message message) throws Exception {
        String jsonMessage = toJson(message);
        sessions.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                    System.out.println("Sent message to " + session.getId() + ": " + jsonMessage);
                } catch (IOException e) {
                    System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void cleanup() {
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing WebSocket session: " + e.getMessage());
            }
        });
        sessions.clear();
        System.out.println("All WebSocket sessions closed");
    }

    @Override
    public boolean isActive() {
        return !sessions.isEmpty();
    }

    /**
     * Get the number of active WebSocket sessions
     * @return The number of active sessions
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }
}
