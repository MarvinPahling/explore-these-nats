package de.ostfalia.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ostfalia.backend.domain.Message;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractConnectionService {

    protected ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Send a message through the connection
     * @param message The Message entity to send
     * @throws Exception if sending fails
     */
    public abstract void sendMessage(Message message) throws Exception;

    /**
     * Subscribe to incoming messages from the connection
     * @return Observable<String> that emits incoming messages as JSON strings
     */
    public abstract Observable<String> subscribe();

    /**
     * Subscribe to incoming messages and parse them as Message objects
     * @return Observable<Message> that emits parsed Message objects
     */
    public Observable<Message> subscribeToMessages() {
        return subscribe()
            .map(json -> objectMapper.readValue(json, Message.class))
            .onErrorResumeNext(error -> {
                System.err.println("Error parsing message: " + error.getMessage());
                return Observable.empty();
            });
    }

    /**
     * Cleanup and close connections
     */
    public abstract void cleanup();

    /**
     * Check if the connection is active/available
     * @return true if connection is active
     */
    public abstract boolean isActive();

    /**
     * Helper method to serialize Message to JSON string
     * Uses Spring-managed ObjectMapper for consistent serialization
     * @param message The Message entity to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    protected String toJson(Message message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }

    /**
     * Helper method to deserialize JSON string to Message
     * @param json The JSON string to deserialize
     * @return Message object
     * @throws JsonProcessingException if deserialization fails
     */
    protected Message fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Message.class);
    }
}
