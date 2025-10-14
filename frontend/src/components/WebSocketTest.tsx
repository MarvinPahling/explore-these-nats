import { useEffect } from "react";
import { useWebSocketStore } from "../store/websocketStore";
import { websocketService } from "../services/websocketConnection";

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
				<h1 className="text-xl font-semibold mb-4 text-gray-700">
					WebSocket Test Value
				</h1>
				<div className="mb-4">
					<span
						className={`inline-block px-3 py-1 rounded-full text-sm ${
							status === "Connected"
								? "bg-green-100 text-green-800"
								: status === "Connecting..."
									? "bg-yellow-100 text-yellow-800"
									: "bg-red-100 text-red-800"
						}`}
					>
						{status}
					</span>
				</div>
				<div className="text-4xl font-bold text-blue-600">
					{value !== null ? value : "Waiting for data..."}
				</div>
			</div>
		</div>
	);
}
