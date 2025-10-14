import { create } from "zustand";
import type { ConnectionStatus } from "../types/connection";

interface NatsState {
	value: number | null;
	status: ConnectionStatus;
	setValue: (value: number | null) => void;
	setStatus: (status: ConnectionStatus) => void;
}

export const useNatsStore = create<NatsState>((set) => ({
	value: null,
	status: "Connecting...",
	setValue: (value) => set({ value }),
	setStatus: (status) => set({ status }),
}));
