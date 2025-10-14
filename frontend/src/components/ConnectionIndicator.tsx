import type { ConnectionStatus } from "../types/connection";

interface ConnectionIndicatorProps {
	status: ConnectionStatus;
}

export default function ConnectionIndicator({ status }: ConnectionIndicatorProps) {
	const getStatusStyles = () => {
		switch (status) {
			case "Connected":
				return "bg-green-100 text-green-800";
			case "Connecting...":
				return "bg-yellow-100 text-yellow-800";
			case "Error":
				return "bg-red-100 text-red-800";
			case "Disconnected":
				return "bg-gray-100 text-gray-800";
			default:
				return "bg-gray-100 text-gray-800";
		}
	};

	return (
		<div className="mb-4">
			<span className={`inline-block px-3 py-1 rounded-full text-sm ${getStatusStyles()}`}>
				{status}
			</span>
		</div>
	);
}
