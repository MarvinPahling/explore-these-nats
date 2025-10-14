import { useEffect } from "react";
import { natsService } from "../services/natsConnection";
import { useNatsStore } from "../store/natsStore";

export default function NatsSubscriber() {
	const value = useNatsStore((state) => state.value);

	useEffect(() => {
		const setupSubscription = async () => {
			try {
				if (!natsService.isConnected()) {
					await natsService.connect();
				}

				await natsService.subscribe("updates", (data: any) => {
					if (data.test !== undefined) {
						useNatsStore.getState().setValue(data.test);
					}
				});
			} catch (err) {
				console.error("Subscription error:", err);
			}
		};

		setupSubscription();

		return () => {
			natsService.unsubscribe("updates");
		};
	}, []);

	return (
		<div className="bg-gray-100 flex items-center justify-center p-4">
			<div className="bg-white rounded-lg shadow-lg p-8 text-center w-full">
				<h1 className="text-xl font-semibold mb-4 text-gray-700">NATS Test Value</h1>
				<div className="text-4xl font-bold text-blue-600">
					{value !== null ? value : "Waiting..."}
				</div>
			</div>
		</div>
	);
}
