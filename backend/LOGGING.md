# Logging Configuration

This backend application uses **SLF4J** with **Logback** for logging, which is the industry-standard logging solution for Spring Boot applications.

## Features

### 1. **Structured Logging**
- Uses SLF4J API for logging throughout the application
- Parameterized logging for better performance
- Consistent log formatting across all classes

### 2. **Multiple Output Formats**
- **Development**: Human-readable console output with timestamps and log levels
- **Production**: JSON-formatted logs (via Logstash encoder) for easy parsing and integration with log aggregation tools

### 3. **Log Levels**
Configured via `application.properties`:
- **INFO**: Default level for application logs
- **DEBUG**: Detailed logs for troubleshooting (can be enabled per-package)
- **WARN**: Warning messages from third-party libraries
- **ERROR**: Error conditions with full stack traces

### 4. **File-based Logging**
- Main log file: `logs/backend.log`
- Error-specific log: `logs/backend-error.log`
- Automatic log rotation:
  - Max file size: 10MB
  - History: 30 days
  - Total cap: 1GB (main), 500MB (errors)

### 5. **Async Logging**
- Uses async appenders for improved performance
- Non-blocking I/O for file writes

## Configuration Files

### `logback-spring.xml`
Main logging configuration with:
- Console appenders (plain and JSON)
- Rolling file appenders
- Async wrappers
- Profile-specific configurations (dev/prod)
- Package-level logger settings

### `application.properties`
Runtime logging configuration:
```properties
# Adjust logging levels
logging.level.root=INFO
logging.level.de.ostfalia.backend=INFO

# Enable debug logging for specific services
# logging.level.de.ostfalia.backend.service.WebSocketService=DEBUG
# logging.level.de.ostfalia.backend.service.NatsPublisherService=DEBUG
```

## Usage in Code

All source files now use SLF4J loggers:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {
    private static final Logger log = LoggerFactory.getLogger(MyService.class);

    public void doSomething() {
        log.info("Starting operation");
        log.debug("Processing item: {}", item);
        log.error("Error occurred: {}", message, exception);
    }
}
```

## Running with Different Profiles

### Development (default)
```bash
./gradlew bootRun
```
Outputs human-readable logs to console + files.

### Production
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```
or
```bash
java -jar build/libs/backend-*.jar --spring.profiles.active=prod
```
Outputs JSON-formatted logs suitable for log aggregation tools (ELK, Splunk, etc.).

## Log File Location

By default, logs are written to the `logs/` directory in the project root:
- `logs/backend.log` - All logs
- `logs/backend-error.log` - Error logs only
- Rotated files: `logs/backend-YYYY-MM-DD.N.log`

The `logs/` directory is git-ignored.

## Debugging Tips

1. **Enable debug logging for specific packages:**
   Edit `application.properties`:
   ```properties
   logging.level.de.ostfalia.backend.service=DEBUG
   ```

2. **Change to DEBUG globally (not recommended for production):**
   ```properties
   logging.level.root=DEBUG
   ```

3. **View logs in real-time:**
   ```bash
   tail -f logs/backend.log
   ```

## Integration with Monitoring Tools

The JSON logging format (production profile) is compatible with:
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Splunk**
- **Datadog**
- **CloudWatch Logs**
- Any JSON log parser

Example JSON log entry:
```json
{
  "@timestamp": "2025-10-14T23:16:06.071Z",
  "@version": "1",
  "message": "NATS connection closed successfully",
  "logger_name": "de.ostfalia.backend.service.NatsPublisherService",
  "thread_name": "SpringApplicationShutdownHook",
  "level": "INFO",
  "level_value": 20000
}
```

## Dependencies

Added to `build.gradle.kts`:
```kotlin
// Logstash encoder for JSON logging
implementation("net.logstash.logback:logstash-logback-encoder:8.0")
```

SLF4J and Logback are included automatically with `spring-boot-starter-web`.
