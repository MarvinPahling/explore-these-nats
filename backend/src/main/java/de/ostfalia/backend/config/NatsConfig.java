package de.ostfalia.backend.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.ConnectException;

@Configuration
public class NatsConfig {

    private static final Logger log = LoggerFactory.getLogger(NatsConfig.class);

    @Value("${nats.server.url:nats://localhost:4222}")
    private String natsServerUrl;

    @Bean
    public Connection natsConnection() {
        try {
            log.info("Attempting to connect to NATS server at {}", natsServerUrl);

            Options options = new Options.Builder()
                    .server(natsServerUrl)
                    .connectionName("backend-publisher")
                    .build();

            Connection connection = Nats.connect(options);
            log.info("Successfully connected to NATS server at {}", natsServerUrl);
            return connection;

        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Unable to connect to NATS servers")) {
                log.error("=================================================================");
                log.error("NATS SERVER CONNECTION FAILED");
                log.error("=================================================================");
                log.error("Could not connect to NATS server at: {}", natsServerUrl);
                log.error("Reason: Connection refused - NATS server is not running");
                log.error("");
                log.error("To start NATS server:");
                log.error("  - Using Docker: docker run -p 4222:4222 -p 8222:8222 nats:latest");
                log.error("  - Using local install: nats-server");
                log.error("=================================================================");
                throw new NatsConnectionException(
                    String.format("Failed to connect to NATS server at %s. Please ensure NATS server is running.", natsServerUrl),
                    e
                );
            } else {
                log.error("Unexpected error while connecting to NATS server at {}: {}", natsServerUrl, e.getMessage(), e);
                throw new NatsConnectionException(
                    String.format("Unexpected error connecting to NATS server at %s: %s", natsServerUrl, e.getMessage()),
                    e
                );
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Connection to NATS server at {} was interrupted: {}", natsServerUrl, e.getMessage(), e);
            throw new NatsConnectionException(
                String.format("Connection to NATS server at %s was interrupted", natsServerUrl),
                e
            );
        }
    }
}
