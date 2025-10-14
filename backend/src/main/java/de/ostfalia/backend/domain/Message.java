package de.ostfalia.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Message entity using Java Record for immutability and reduced boilerplate.
 * Jackson automatically handles serialization/deserialization with proper constructor.
 */
public record Message(
    @JsonProperty("test") int value,
    @JsonProperty("timestamp") long timestamp
) {
    /**
     * Convenience constructor that automatically sets the timestamp
     * @param value The message value
     */
    public Message(int value) {
        this(value, Instant.now().toEpochMilli());
    }
}
