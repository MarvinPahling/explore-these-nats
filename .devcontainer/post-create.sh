#!/bin/bash

echo "Setting up development environment..."

# Install Bun for the vscode user
if ! command -v bun &> /dev/null; then
    echo "Installing Bun..."
    curl -fsSL https://bun.sh/install | bash
    export BUN_INSTALL="$HOME/.bun"
    export PATH="$BUN_INSTALL/bin:$PATH"
fi

# Install frontend dependencies
echo "Installing frontend dependencies..."
cd /workspace/frontend
if command -v bun &> /dev/null; then
    bun install
else
    npm install
fi

# Make Gradle wrapper executable
echo "Setting up backend..."
cd /workspace/backend
chmod +x ./gradlew

# Download Gradle dependencies
echo "Downloading backend dependencies..."
./gradlew build -x test || true

echo "Setup complete!"
echo ""
echo "Available services:"
echo "  - NATS Server: localhost:4222"
echo "  - NATS WebSocket: localhost:42070"
echo "  - NATS Monitoring: http://localhost:8222"
echo ""
echo "To start the application:"
echo "  1. Backend: cd backend && ./gradlew bootRun"
echo "  2. Frontend: cd frontend && bun dev"
echo ""
echo "Or use docker-compose: docker-compose up --build"
