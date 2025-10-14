package de.ostfalia.backend.service;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import de.ostfalia.backend.domain.Message;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Bidirectional NATS messaging service supporting both publishing and subscribing.
 * Connects to NATS server and provides reactive message handling via RxJava.
 */
@Service
public class NatsService extends AbstractConnectionService {

    private final Connection natsConnection;
    private final PublishSubject<String> messageSubject;
    private Dispatcher dispatcher;
    private static final String SUBJECT = "updates";

    @Autowired
    public NatsService(Connection natsConnection) {
        this.natsConnection = natsConnection;
        this.messageSubject = PublishSubject.create();
    }

    @PostConstruct
    public void init() {
        // Initialize subscription to the same subject we publish to
        dispatcher = natsConnection.createDispatcher((io.nats.client.Message msg) -> {
            String messageData = new String(msg.getData(), StandardCharsets.UTF_8);
            messageSubject.onNext(messageData);
        });
        dispatcher.subscribe(SUBJECT);
        System.out.println("NATS service initialized - subscribed to subject: " + SUBJECT);
    }

    @Override
    public void sendMessage(Message message) throws Exception {
        String jsonMessage = toJson(message);
        natsConnection.publish(SUBJECT, jsonMessage.getBytes());
        System.out.println("Published message to NATS subject '" + SUBJECT + "': " + jsonMessage);
    }

    @Override
    public Observable<String> subscribe() {
        return messageSubject;
    }

    @Override
    @PreDestroy
    public void cleanup() {
        try {
            if (dispatcher != null) {
                System.out.println("Unsubscribing from NATS subject: " + SUBJECT);
            }
            messageSubject.onComplete();
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
