package de.ostfalia.backend.config;

/**
 * Custom exception thrown when the application fails to connect to the NATS server.
 * This exception wraps underlying connection errors and provides clear error messaging
 * to help diagnose NATS connectivity issues.
 */
public class NatsConnectionException extends RuntimeException {

    /**
     * Constructs a new NatsConnectionException with the specified detail message.
     *
     * @param message the detail message explaining the connection failure
     */
    public NatsConnectionException(String message) {
        super(message);
    }

    /**
     * Constructs a new NatsConnectionException with the specified detail message and cause.
     *
     * @param message the detail message explaining the connection failure
     * @param cause the underlying cause of the connection failure
     */
    public NatsConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
