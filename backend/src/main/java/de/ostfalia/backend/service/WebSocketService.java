package de.ostfalia.backend.service;

import de.ostfalia.backend.domain.Message;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bidirectional WebSocket messaging service supporting both sending and receiving.
 * Manages multiple client connections and provides reactive message handling via RxJava.
 */
@Service
public class WebSocketService extends AbstractConnectionService {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final PublishSubject<String> messageSubject = PublishSubject.create();

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

    /**
     * Handle incoming WebSocket message
     * @param message The message received from a WebSocket client
     */
    public void handleIncomingMessage(String message) {
        System.out.println("Received WebSocket message: " + message);
        messageSubject.onNext(message);
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
    public Observable<String> subscribe() {
        return messageSubject;
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
        messageSubject.onComplete();
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
