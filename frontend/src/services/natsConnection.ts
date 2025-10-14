import {
	connect as natsConnect,
	StringCodec,
	type NatsConnection,
	type Subscription,
} from "nats.ws";
import { useNatsStore } from "../store/natsStore";

class NatsConnectionService {
	private nc: NatsConnection | null = null;
	private subscriptions: Map<string, Subscription> = new Map();
	private sc = StringCodec();
	private serverUrl: string;

	constructor(serverUrl = "ws://localhost:42070") {
		this.serverUrl = serverUrl;
	}

	async connect() {
		try {
			useNatsStore.getState().setStatus("Connecting...");
			this.nc = await natsConnect({ servers: this.serverUrl });
			useNatsStore.getState().setStatus("Connected");
			console.log("Connected to NATS server");
		} catch (err) {
			console.error("Connection error:", err);
			useNatsStore.getState().setStatus("Error");
			throw err;
		}
	}

	async subscribe(subject: string, callback: (data: unknown) => void) {
		if (!this.nc) {
			throw new Error("Not connected to NATS server");
		}

		const sub = this.nc.subscribe(subject);
		this.subscriptions.set(subject, sub);

		(async () => {
			for await (const m of sub) {
				try {
					const data = JSON.parse(this.sc.decode(m.data));
					callback(data);
				} catch (e) {
					console.error("Error parsing message:", e);
				}
			}
		})();

		return sub;
	}

	unsubscribe(subject: string) {
		const sub = this.subscriptions.get(subject);
		if (sub) {
			sub.unsubscribe();
			this.subscriptions.delete(subject);
		}
	}

	async disconnect() {
		for (const [subject] of this.subscriptions) {
			this.unsubscribe(subject);
		}
		if (this.nc) {
			await this.nc.close();
			this.nc = null;
			useNatsStore.getState().setStatus("Disconnected");
			console.log("Disconnected from NATS server");
		}
	}

	isConnected(): boolean {
		return this.nc !== null;
	}

	getConnection(): NatsConnection | null {
		return this.nc;
	}
}

export const natsService = new NatsConnectionService();
