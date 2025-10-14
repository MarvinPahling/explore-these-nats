package de.ostfalia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;

@Service
@EnableScheduling
public class NatsPublisherService {

    private final Connection natsConnection;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private static final String SUBJECT = "updates";

    @Autowired
    public NatsPublisherService(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    @Scheduled(fixedRate = 1000)
    public void publishMessage() {
        try {
            Map<String, Object> message = Map.of("test", random.nextInt(1000));
            String jsonMessage = objectMapper.writeValueAsString(message);

            natsConnection.publish(SUBJECT, jsonMessage.getBytes());
            System.out.println("Published message to NATS subject '" + SUBJECT + "': " + jsonMessage);
        } catch (Exception e) {
            System.err.println("Error publishing message to NATS: " + e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (natsConnection != null) {
                natsConnection.close();
                System.out.println("NATS connection closed");
            }
        } catch (InterruptedException e) {
            System.err.println("Error closing NATS connection: " + e.getMessage());
        }
    }
}
