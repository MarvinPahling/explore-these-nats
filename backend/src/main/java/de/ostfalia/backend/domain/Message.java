package de.ostfalia.backend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;

public class Message {

    @JsonProperty("test")
    private int value;

    @JsonProperty("timestamp")
    private long timestamp;

    public Message() {
        this.timestamp = Instant.now().toEpochMilli();
    }

    public Message(int value) {
        this.value = value;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return value == message.value && timestamp == message.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, timestamp);
    }

    @Override
    public String toString() {
        return "Message{" +
                "value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
