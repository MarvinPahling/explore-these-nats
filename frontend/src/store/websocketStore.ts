import { create } from "zustand";

interface WebSocketState {
	value: number | null;
	status: "Connecting..." | "Connected" | "Error" | "Disconnected";
	setValue: (value: number | null) => void;
	setStatus: (status: "Connecting..." | "Connected" | "Error" | "Disconnected") => void;
}

export const useWebSocketStore = create<WebSocketState>((set) => ({
	value: null,
	status: "Connecting...",
	setValue: (value) => set({ value }),
	setStatus: (status) => set({ status }),
}));
