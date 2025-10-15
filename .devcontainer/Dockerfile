FROM mcr.microsoft.com/devcontainers/base:ubuntu

# Install system dependencies
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
    build-essential \
    curl \
    wget \
    git \
    unzip \
    ca-certificates \
    && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Switch to vscode user to install Bun
USER vscode

# Install Bun for vscode user
RUN curl -fsSL https://bun.sh/install | bash
ENV BUN_INSTALL="/home/vscode/.bun"
ENV PATH="$BUN_INSTALL/bin:$PATH"

# Create workspace directory
USER root
RUN mkdir -p /workspace && chown vscode:vscode /workspace
USER vscode
WORKDIR /workspace
