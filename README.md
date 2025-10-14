# 🎮 Real-time Game State Management

> **Comparing WebSocket vs NATS for multiplayer game development**

---

## 📊 Architecture Comparison

A comprehensive analysis of two real-time communication approaches for a **4-8 player game** with frequent state changes.

---

## 🔌 WebSocket Approach

**Implementation:** [`WebSocketService.java`](backend/src/main/java/de/ostfalia/backend/service/WebSocketService.java)

### Architecture Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        C1[Client 1]
        C2[Client 2]
        C3[Client 3]
        C4[Client N]
    end

    subgraph "Backend Server"
        WSH[TestWebSocketHandler<br/>handler/TestWebSocketHandler.java]
        WSS[WebSocketService<br/>service/WebSocketService.java]
        SM[Session Manager<br/>ConcurrentHashMap]
        GL[Game Logic<br/>@Scheduled Generator]
    end

    C1 <-->|WebSocket Connection| WSH
    C2 <-->|WebSocket Connection| WSH
    C3 <-->|WebSocket Connection| WSH
    C4 <-->|WebSocket Connection| WSH

    WSH -->|Register/Unregister| WSS
    WSS -->|Store Sessions| SM
    GL -->|Generate State| WSS
    WSS -->|Broadcast to All| SM
    SM -.->|Iterate & Send| C1
    SM -.->|Iterate & Send| C2
    SM -.->|Iterate & Send| C3
    SM -.->|Iterate & Send| C4

    style C1 fill:#e1f5ff
    style C2 fill:#e1f5ff
    style C3 fill:#e1f5ff
    style C4 fill:#e1f5ff
    style WSS fill:#fff4e6
    style SM fill:#ffe6e6
```

### Message Flow

```mermaid
sequenceDiagram
    participant C1 as Client 1
    participant C2 as Client 2
    participant H as TestWebSocketHandler
    participant S as WebSocketService
    participant GL as Game Logic

    Note over C1,H: Connection Phase
    C1->>H: Connect WebSocket
    H->>S: registerSession(session)
    S->>S: sessions.put(id, session)

    C2->>H: Connect WebSocket
    H->>S: registerSession(session)
    S->>S: sessions.put(id, session)

    Note over GL,C2: Broadcast Phase (every 1000ms)
    GL->>S: @Scheduled broadcastMessage()
    S->>S: Generate Message(randomValue)
    S->>S: Iterate over sessions
    S->>C1: sendMessage(jsonMessage)
    S->>C2: sendMessage(jsonMessage)

    Note over C1,S: Disconnection Phase
    C1->>H: Disconnect
    H->>S: unregisterSession(session)
    S->>S: sessions.remove(id)
```

### ✅ Advantages

- **Simple Architecture** - Direct persistent connection between client and server
- **Low Latency** - No intermediary message broker
- **Native Support** - Built-in Spring Boot WebSocket support
- **Easy Implementation** - Straightforward to set up and configure
- **Direct Client Management** - Backend tracks sessions directly (WebSocketService.java:20)
- **Lightweight** - No additional infrastructure needed for small-scale deployments

### ❌ Disadvantages

- **Tight Coupling** - Backend handles all broadcast logic directly
- **Limited Scalability** - Difficult to scale horizontally (sessions tied to specific server instance)
- **No Persistence** - Messages lost if client disconnects
- **Manual Session Management** - Requires managing ConcurrentHashMap of sessions manually
- **Single Point of Failure** - All connections drop if backend restarts

---

## 🚀 NATS Approach

**Implementation:** [`NatsPublisherService.java`](backend/src/main/java/de/ostfalia/backend/service/NatsPublisherService.java)

### Architecture Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        C1[Client 1<br/>Subscriber]
        C2[Client 2<br/>Subscriber]
        C3[Client 3<br/>Subscriber]
        C4[Client N<br/>Subscriber]
    end

    subgraph "NATS Message Broker"
        NATS[NATS Server<br/>Subject: 'updates']
        SUB[Subject Router]
    end

    subgraph "Backend Server"
        NPS[NatsPublisherService<br/>service/NatsPublisherService.java]
        GL[Game Logic<br/>@Scheduled Generator]
        NC[NATS Connection]
    end

    GL -->|Generate State| NPS
    NPS -->|Serialize to JSON| NC
    NC -->|Publish to 'updates'| NATS
    NATS -->|Route Messages| SUB
    SUB -.->|Push Update| C1
    SUB -.->|Push Update| C2
    SUB -.->|Push Update| C3
    SUB -.->|Push Update| C4

    C1 -.->|Subscribe to 'updates'| NATS
    C2 -.->|Subscribe to 'updates'| NATS
    C3 -.->|Subscribe to 'updates'| NATS
    C4 -.->|Subscribe to 'updates'| NATS

    style C1 fill:#e1f5ff
    style C2 fill:#e1f5ff
    style C3 fill:#e1f5ff
    style C4 fill:#e1f5ff
    style NPS fill:#fff4e6
    style NATS fill:#e8f5e9
    style SUB fill:#e8f5e9
```

### Message Flow

```mermaid
sequenceDiagram
    participant C1 as Client 1
    participant C2 as Client 2
    participant NATS as NATS Server
    participant S as NatsPublisherService
    participant GL as Game Logic

    Note over C1,NATS: Subscription Phase
    C1->>NATS: Subscribe to 'updates'
    NATS->>NATS: Register subscriber
    C2->>NATS: Subscribe to 'updates'
    NATS->>NATS: Register subscriber

    Note over GL,C2: Publishing Phase (every 1000ms)
    GL->>S: @Scheduled publishMessage()
    S->>S: Generate Message(randomValue)
    S->>S: Serialize to JSON
    S->>NATS: Publish to 'updates' subject
    NATS->>NATS: Route to subscribers
    NATS->>C1: Push message
    NATS->>C2: Push message

    Note over C1,NATS: Unsubscribe Phase
    C1->>NATS: Unsubscribe/Disconnect
    NATS->>NATS: Remove subscriber
```

### ✅ Advantages

- **Pub/Sub Model** - Clean separation between publishers and subscribers
- **Horizontal Scalability** - Multiple backend instances can publish; NATS handles routing
- **Message Buffering** - Efficiently handles bursts of messages
- **Decoupled Architecture** - Backend doesn't manage individual client connections
- **Subject-Based Routing** - Easy implementation of room/game-specific channels
- **Quality of Service** - Better guarantees for message delivery
- **Future-Proof** - Easy to add features like game replays, spectators, or multiple game rooms

### ❌ Disadvantages

- **Additional Infrastructure** - Requires NATS server (though already set up in this project)
- **Increased Complexity** - Extra layer of abstraction
- **Minimal Overhead** - Small additional latency from message broker (typically <1ms)

---

## 🏆 Recommendation: **NATS**

### Why NATS is Better for This Use Case

#### 1. **Game Rooms & Lobbies**

With NATS subjects like `game.room1.state` and `game.room2.state`, you can easily support multiple concurrent games. WebSocket would require complex routing logic.

#### 2. **State Synchronization**

NATS pub/sub ensures all clients get the same state updates reliably, even when scaling to multiple backend instances.

#### 3. **Scalability**

Even though 4-8 players is small-scale, NATS makes it trivial to support multiple game instances or grow later.

#### 4. **Performance**

NATS is extremely fast (millions of messages/sec) with negligible latency. For game state updates every 16-33ms (30-60 FPS), this is perfect.

#### 5. **Decoupling**

Game logic can publish state changes without worrying about which clients are connected—NATS handles delivery.

#### 6. **Feature Extensibility**

Easy to add spectator mode, game replay, or server-side game state validation later.

---

## 🎯 Recommended Implementation Pattern

### **Hybrid Approach: WebSocket + NATS**

Combine both technologies for optimal performance:

```mermaid
graph TB
    subgraph "Client Layer"
        C1[Client 1]
        C2[Client 2]
        C3[Client N]
    end

    subgraph "Backend Server"
        WSH[WebSocket Handler<br/>Receives Player Actions]
        GL[Game Logic<br/>Process Actions & Generate State]
        NPS[NATS Publisher<br/>Broadcast State Updates]
    end

    subgraph "NATS Broker"
        NATS[NATS Server<br/>Subject: game.roomId.state]
    end

    C1 -->|Player Action| WSH
    C2 -->|Player Action| WSH
    C3 -->|Player Action| WSH

    WSH -->|Process| GL
    GL -->|State Change| NPS
    NPS -->|Publish| NATS

    NATS -.->|State Update| C1
    NATS -.->|State Update| C2
    NATS -.->|State Update| C3

    C1 -.->|Subscribe| NATS
    C2 -.->|Subscribe| NATS
    C3 -.->|Subscribe| NATS

    style C1 fill:#e1f5ff
    style C2 fill:#e1f5ff
    style C3 fill:#e1f5ff
    style WSH fill:#fff4e6
    style GL fill:#fff4e6
    style NPS fill:#fff4e6
    style NATS fill:#e8f5e9
```

### Message Flow

```mermaid
sequenceDiagram
    participant C1 as Client 1
    participant C2 as Client 2
    participant WS as WebSocket Handler
    participant GL as Game Logic
    participant NATS as NATS Server

    Note over C1,NATS: Setup Phase
    C1->>WS: Connect WebSocket
    C1->>NATS: Subscribe to 'game.room1.state'
    C2->>WS: Connect WebSocket
    C2->>NATS: Subscribe to 'game.room1.state'

    Note over C1,C2: Player Action Flow
    C1->>WS: Send Player Action (e.g., move)
    WS->>GL: Process Action
    GL->>GL: Update Game State
    GL->>NATS: Publish new state to 'game.room1.state'
    NATS->>C1: Push State Update
    NATS->>C2: Push State Update

    Note over C2,NATS: Another Player Action
    C2->>WS: Send Player Action (e.g., attack)
    WS->>GL: Process Action
    GL->>GL: Update Game State
    GL->>NATS: Publish new state to 'game.room1.state'
    NATS->>C1: Push State Update
    NATS->>C2: Push State Update
```

### Benefits of Hybrid Approach

- **WebSocket** for client-to-server commands (low latency, simple request/response)
- **NATS** for server-to-client state broadcasts (reliable, scalable, decoupled)
- **Bidirectional Communication** - WebSocket for actions, NATS for state synchronization
- **Scalability** - Multiple backend instances can handle actions and publish to NATS
- **Best of Both Worlds** - Simplicity of WebSocket + power of pub/sub messaging

---

## 📁 Project Structure

```
backend/src/main/java/de/ostfalia/backend/
├── domain/
│   └── Message.java                    # Message domain model
├── handler/
│   └── TestWebSocketHandler.java       # WebSocket handler
├── service/
│   ├── NatsPublisherService.java       # NATS publisher service
│   └── WebSocketService.java           # WebSocket service
```

---

## 🛠️ Technology Stack

- **Spring Boot** - Backend framework
- **NATS** - Message broker for pub/sub
- **WebSocket** - Real-time bidirectional communication
- **Java** - Primary programming language

---

## 🐳 Docker/Podman

Build and run all services (frontend, backend, NATS):

### Docker

```bash
docker-compose up --build

```

### Podman

```bash
podman-compose up --build
```

Access at:

- Frontend: http://localhost:3000
- Backend: http://localhost:42069
- NATS: nats://localhost:4222

---

## 📝 License

This is a technical evaluation and implementation guide for real-time multiplayer game architecture.
