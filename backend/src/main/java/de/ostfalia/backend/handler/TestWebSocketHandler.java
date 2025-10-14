package de.ostfalia.backend.handler;
import de.ostfalia.backend.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class TestWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;

    @Autowired
    public TestWebSocketHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        webSocketService.registerSession(session);
        System.out.println("WebSocket connection established: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        webSocketService.unregisterSession(session);
        System.out.println("WebSocket connection closed: " + session.getId());
    }
}
