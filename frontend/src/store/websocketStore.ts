import { create } from "zustand";
import type { ConnectionStatus } from "../types/connection";

interface WebSocketState {
	value: number | null;
	status: ConnectionStatus;
	setValue: (value: number | null) => void;
	setStatus: (status: ConnectionStatus) => void;
}

export const useWebSocketStore = create<WebSocketState>((set) => ({
	value: null,
	status: "Connecting...",
	setValue: (value) => set({ value }),
	setStatus: (status) => set({ status }),
}));
