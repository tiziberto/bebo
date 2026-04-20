import { defineStore } from "pinia";
import { ref } from "vue";

export interface ToastItem {
  id: number;
  title: string;
  description?: string;
  variant?: "default" | "destructive";
  open: boolean;
}

let count = 0;

export const useToastStore = defineStore("toast", () => {
  const toasts = ref<ToastItem[]>([]);

  function show(opts: { title: string; description?: string; variant?: "default" | "destructive" }) {
    const id = ++count;
    const item: ToastItem = { id, open: true, ...opts };
    toasts.value = [item, ...toasts.value].slice(0, 1);

    setTimeout(() => {
      dismiss(id);
    }, 5000);
  }

  function dismiss(id: number) {
    const t = toasts.value.find((t) => t.id === id);
    if (t) t.open = false;
    setTimeout(() => {
      toasts.value = toasts.value.filter((t) => t.id !== id);
    }, 300);
  }

  return { toasts, show, dismiss };
});
