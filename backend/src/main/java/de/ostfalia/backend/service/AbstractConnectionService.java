package de.ostfalia.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ostfalia.backend.domain.Message;
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
}
