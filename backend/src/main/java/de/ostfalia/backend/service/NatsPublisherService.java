package de.ostfalia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ostfalia.backend.domain.Message;
import io.nats.client.Connection;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@EnableScheduling
public class NatsPublisherService {

    private static final Logger log = LoggerFactory.getLogger(NatsPublisherService.class);
    private static final String SUBJECT = "updates";

    private final Connection natsConnection;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    @Autowired
    public NatsPublisherService(Connection natsConnection) {
        this.natsConnection = natsConnection;
        log.info("NatsPublisherService initialized");
    }

    @Scheduled(fixedRate = 1000)
    public void publishMessage() {
        try {
            Message message = new Message(random.nextInt(1000));
            String jsonMessage = objectMapper.writeValueAsString(message);

            natsConnection.publish(SUBJECT, jsonMessage.getBytes());
            log.debug("Published message to NATS subject '{}': {}", SUBJECT, jsonMessage);
        } catch (Exception e) {
            log.error("Error publishing message to NATS subject '{}': {}", SUBJECT, e.getMessage(), e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (natsConnection != null) {
                natsConnection.close();
                log.info("NATS connection closed successfully");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error closing NATS connection: {}", e.getMessage(), e);
        }
    }
}
