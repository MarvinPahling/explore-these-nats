# Use official NATS server image
FROM nats:2.10-alpine

# Copy custom configuration
COPY nats-server.conf /etc/nats/nats-server.conf

# Expose standard NATS port and WebSocket port
EXPOSE 4222 42070

# Run NATS server with custom config
CMD ["nats-server", "-c", "/etc/nats/nats-server.conf"]
