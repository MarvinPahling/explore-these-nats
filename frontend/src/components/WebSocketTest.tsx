import { useEffect } from "react";
import { useWebSocketStore } from "../store/websocketStore";
import { websocketService } from "../services/websocketConnection";
import ConnectionIndicator from "./ConnectionIndicator";

export default function WebSocketTest() {
	const { value, status } = useWebSocketStore();

	useEffect(() => {
		const handlerId = "websocket-test-component";

		if (!websocketService.isConnected()) {
			websocketService.connect();
		}

		websocketService.registerMessageHandler(handlerId, (data: any) => {
			if (data.test !== undefined && typeof data.test === "number") {
				useWebSocketStore.getState().setValue(data.test);
			}
		});

		return () => {
			websocketService.unregisterMessageHandler(handlerId);
		};
	}, []);

	return (
		<div className="bg-gray-100 flex items-center justify-center p-4">
			<div className="bg-white rounded-lg shadow-lg p-8 text-center w-full">
				<h1 className="text-xl font-semibold mb-4 text-gray-700">WebSocket Test Value</h1>
				<ConnectionIndicator status={status} />
				<div className="text-4xl font-bold text-blue-600">
					{value !== null ? value : "Waiting for data..."}
				</div>
			</div>
		</div>
	);
}
