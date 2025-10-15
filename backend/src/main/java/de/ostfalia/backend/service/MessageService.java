package de.ostfalia.backend.service;

import de.ostfalia.backend.domain.Message;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Central service for managing bidirectional message flow across all connection services. Handles
 * both outbound broadcasts (sending) and inbound subscriptions (receiving).
 */
@Service
@EnableScheduling
public class MessageService {

  private final List<AbstractConnectionService> connectionServices;
  private final Random random = new Random();

  @Autowired
  public MessageService(List<AbstractConnectionService> connectionServices) {
    this.connectionServices = connectionServices;
  }

  @PostConstruct
  public void init() {
    System.out.println(
        "Initializing MessageService with " + connectionServices.size() + " connection services");

    // Subscribe to incoming messages from all services
    for (AbstractConnectionService service : connectionServices) {
      String serviceName = service.getClass().getSimpleName();

      service
          .subscribe()
          .subscribe(
              message -> handleIncomingMessage(serviceName, message),
              error ->
                  System.err.println(
                      "Error in " + serviceName + " subscription: " + error.getMessage()));

      System.out.println("Subscribed to incoming messages from: " + serviceName);
    }
  }

  /**
   * Handle incoming messages from any connection service
   *
   * @param serviceName Name of the service that received the message
   * @param message The raw JSON message
   */
  private void handleIncomingMessage(String serviceName, String message) {
    System.out.println("[" + serviceName + "] Received: " + message);
    // Add custom logic here to process incoming messages
  }

  /**
   * Creates a new message with a random value
   *
   * @return Message object
   */
  public Message createMessage() {
    return new Message(random.nextInt(1000));
  }

  /** Scheduled task that broadcasts messages to all active connection services */
  @Scheduled(fixedRate = 1000)
  public void broadcastMessages() {
    Message message = createMessage();

    for (AbstractConnectionService service : connectionServices) {
      if (service.isActive()) {
        try {
          service.sendMessage(message);
        } catch (Exception e) {
          System.err.println(
              "Error sending message via "
                  + service.getClass().getSimpleName()
                  + ": "
                  + e.getMessage());
        }
      }
    }
  }

  /**
   * Bridge messages from one service to all other active services Call this method to enable
   * automatic message forwarding
   */
  public void enableBridging(AbstractConnectionService sourceService) {
    String sourceName = sourceService.getClass().getSimpleName();

    sourceService
        .subscribeToMessages()
        .subscribe(
            message -> {
              System.out.println("Bridging message from " + sourceName + " to other services");

              for (AbstractConnectionService targetService : connectionServices) {
                if (targetService != sourceService && targetService.isActive()) {
                  try {
                    targetService.sendMessage(message);
                  } catch (Exception e) {
                    System.err.println(
                        "Error bridging to "
                            + targetService.getClass().getSimpleName()
                            + ": "
                            + e.getMessage());
                  }
                }
              }
            });
  }
}
