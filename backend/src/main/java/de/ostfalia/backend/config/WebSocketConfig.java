package de.ostfalia.backend.config;

import de.ostfalia.backend.handler.TestWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TestWebSocketHandler testWebSocketHandler;

    public WebSocketConfig(TestWebSocketHandler testWebSocketHandler) {
        this.testWebSocketHandler = testWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(testWebSocketHandler, "/ws/test")
                .setAllowedOrigins("*");
    }
}
