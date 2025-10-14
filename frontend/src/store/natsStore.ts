import { create } from "zustand";

interface NatsState {
	value: number | null;
	status: "Connecting..." | "Connected" | "Error" | "Disconnected";
	setValue: (value: number | null) => void;
	setStatus: (
		status: "Connecting..." | "Connected" | "Error" | "Disconnected",
	) => void;
}

export const useNatsStore = create<NatsState>((set) => ({
	value: null,
	status: "Connecting...",
	setValue: (value) => set({ value }),
	setStatus: (status) => set({ status }),
}));
