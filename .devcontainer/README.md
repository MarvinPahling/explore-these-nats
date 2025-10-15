# Dev Container Setup

This devcontainer provides a complete development environment for the NATS Game State Management project.

## What's Included

### Languages & Runtimes
- **Java 21** with Gradle 8.12
- **Node.js 22**
- **Bun** (latest) - for faster package management

### Tools
- Git
- Docker-in-Docker (for running docker-compose inside the container)
- VS Code Java extensions
- Spring Boot extensions
- Biome formatter/linter
- Tailwind CSS IntelliSense

### Services
- **NATS Server** - Running on port 4222
- **NATS WebSocket** - Running on port 42070
- **NATS Monitoring** - Available at http://localhost:8222

## Getting Started

### Prerequisites
- VS Code with the "Dev Containers" extension
- Docker Desktop or compatible Docker runtime

### Opening the Project

1. Open this folder in VS Code
2. When prompted, click "Reopen in Container" (or run command: `Dev Containers: Reopen in Container`)
3. Wait for the container to build and the post-create script to complete
4. NATS server will automatically start

### Running the Application

#### Backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```

The backend will be available at http://localhost:42069

#### Frontend (React + Vite)
```bash
cd frontend
bun dev
```

The frontend will be available at http://localhost:3000

#### Full Stack with Docker Compose
From the workspace root:
```bash
docker-compose up --build
```

## Port Mappings

| Port  | Service              | Description                    |
|-------|----------------------|--------------------------------|
| 3000  | Frontend             | React app (Vite dev server)    |
| 4222  | NATS                 | NATS client connections        |
| 8222  | NATS Monitoring      | NATS server monitoring UI      |
| 42069 | Backend              | Spring Boot API                |
| 42070 | NATS WebSocket       | NATS WebSocket connections     |

## Development Tips

### Backend Development
- Hot reload is enabled via Spring Boot DevTools
- Run tests: `./gradlew test`
- Build: `./gradlew build`

### Frontend Development
- Fast refresh is enabled via Vite
- Run tests: `bun test`
- Type check: `tsc --noEmit`
- Format/Lint: `bun run check`

### NATS Monitoring
Visit http://localhost:8222 to see:
- Server connections
- Message statistics
- Subject subscriptions

## Troubleshooting

### NATS Connection Issues
If the backend can't connect to NATS, ensure the NATS container is running:
```bash
docker ps | grep nats-server-dev
```

### Port Already in Use
If you see port binding errors, check what's using the ports:
```bash
lsof -i :4222
lsof -i :42069
lsof -i :3000
```

### Rebuilding the Container
If you need to rebuild from scratch:
1. Command Palette: `Dev Containers: Rebuild Container`
2. Or manually: `docker-compose -f .devcontainer/docker-compose.yml down --volumes`

## Architecture

This project compares WebSocket vs NATS approaches for real-time multiplayer game state management. See the main README.md for detailed architecture diagrams and recommendations.
