package de.ostfalia.backend.service;

import de.ostfalia.backend.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@EnableScheduling
public class MessageService {

    private final List<AbstractConnectionService> connectionServices;
    private final Random random = new Random();

    @Autowired
    public MessageService(List<AbstractConnectionService> connectionServices) {
        this.connectionServices = connectionServices;
    }

    /**
     * Creates a new message with a random value
     * @return Message object
     */
    public Message createMessage() {
        return new Message(random.nextInt(1000));
    }

    /**
     * Scheduled task that broadcasts messages to all active connection services
     */
    @Scheduled(fixedRate = 1000)
    public void broadcastMessages() {
        Message message = createMessage();

        for (AbstractConnectionService service : connectionServices) {
            if (service.isActive()) {
                try {
                    service.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("Error sending message via " +
                        service.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }
}
