import { useWebSocketStore } from "../store/websocketStore";

class WebSocketConnectionService {
	private ws: WebSocket | null = null;
	private serverUrl: string;
	private messageHandlers: Map<string, (data: unknown) => void> = new Map();

	constructor(serverUrl = "ws://localhost:42069/ws/test") {
		this.serverUrl = serverUrl;
	}

	connect() {
		try {
			useWebSocketStore.getState().setStatus("Connecting...");
			this.ws = new WebSocket(this.serverUrl);

			this.ws.onopen = () => {
				useWebSocketStore.getState().setStatus("Connected");
				console.log("WebSocket connected");
			};

			this.ws.onmessage = (event) => {
				try {
					const data = JSON.parse(event.data);
					// Call all registered message handlers
					for (const handler of this.messageHandlers.values()) {
						handler(data);
					}
				} catch (error) {
					console.error("Error parsing WebSocket message:", error);
				}
			};

			this.ws.onerror = (error) => {
				useWebSocketStore.getState().setStatus("Error");
				console.error("WebSocket error:", error);
			};

			this.ws.onclose = () => {
				useWebSocketStore.getState().setStatus("Disconnected");
				console.log("WebSocket disconnected");
			};
		} catch (err) {
			console.error("Connection error:", err);
			useWebSocketStore.getState().setStatus("Error");
			throw err;
		}
	}

	registerMessageHandler(id: string, handler: (data: unknown) => void) {
		this.messageHandlers.set(id, handler);
	}

	unregisterMessageHandler(id: string) {
		this.messageHandlers.delete(id);
	}

	disconnect() {
		if (this.ws) {
			this.ws.close();
			this.ws = null;
			this.messageHandlers.clear();
			useWebSocketStore.getState().setStatus("Disconnected");
			console.log("WebSocket disconnected");
		}
	}

	isConnected(): boolean {
		return this.ws !== null && this.ws.readyState === WebSocket.OPEN;
	}

	getConnection(): WebSocket | null {
		return this.ws;
	}

	send(data: unknown) {
		if (this.ws && this.ws.readyState === WebSocket.OPEN) {
			this.ws.send(JSON.stringify(data));
		} else {
			console.error("WebSocket is not connected");
		}
	}
}

export const websocketService = new WebSocketConnectionService();
