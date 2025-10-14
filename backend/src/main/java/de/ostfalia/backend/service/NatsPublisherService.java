package de.ostfalia.backend.service;

import io.nats.client.Connection;
import de.ostfalia.backend.domain.Message;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NatsPublisherService extends AbstractConnectionService {

    private final Connection natsConnection;
    private static final String SUBJECT = "updates";

    @Autowired
    public NatsPublisherService(Connection natsConnection) {
        this.natsConnection = natsConnection;
    }

    @Override
    public void sendMessage(Message message) throws Exception {
        String jsonMessage = toJson(message);
        natsConnection.publish(SUBJECT, jsonMessage.getBytes());
        System.out.println("Published message to NATS subject '" + SUBJECT + "': " + jsonMessage);
    }

    @Override
    @PreDestroy
    public void cleanup() {
        try {
            if (natsConnection != null && natsConnection.getStatus() == Connection.Status.CONNECTED) {
                natsConnection.close();
                System.out.println("NATS connection closed");
            }
        } catch (InterruptedException e) {
            System.err.println("Error closing NATS connection: " + e.getMessage());
        }
    }

    @Override
    public boolean isActive() {
        return natsConnection != null && natsConnection.getStatus() == Connection.Status.CONNECTED;
    }
}
