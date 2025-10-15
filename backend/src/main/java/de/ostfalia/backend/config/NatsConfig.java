package de.ostfalia.backend.config;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsConfig {

  @Value("${nats.server.url:nats://localhost:4222}")
  private String natsServerUrl;

  @Bean
  public Connection natsConnection() throws IOException, InterruptedException {
    Options options =
        new Options.Builder().server(natsServerUrl).connectionName("backend-publisher").build();

    Connection connection = Nats.connect(options);
    System.out.println("Connected to NATS server at " + natsServerUrl);
    return connection;
  }
}
