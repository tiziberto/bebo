<script setup lang="ts">
import { useToastStore } from "@/stores/toast";
import { cn } from "@/lib/utils";
import { X } from "lucide-vue-next";

const store = useToastStore();
</script>

<template>
  <div class="fixed top-0 right-0 z-[100] flex max-h-screen w-full flex-col-reverse p-4 sm:right-0 sm:top-auto sm:bottom-0 sm:flex-col md:max-w-[420px]">
    <TransitionGroup name="toast">
      <div
        v-for="toast in store.toasts"
        :key="toast.id"
        :class="cn(
          'group pointer-events-auto relative flex w-full items-center justify-between space-x-4 overflow-hidden rounded-md border p-6 pr-8 shadow-lg transition-all',
          toast.variant === 'destructive'
            ? 'border-destructive bg-destructive text-destructive-foreground'
            : 'border bg-background text-foreground'
        )"
      >
        <div class="grid gap-1">
          <div class="text-sm font-semibold">{{ toast.title }}</div>
          <div v-if="toast.description" class="text-sm opacity-90">{{ toast.description }}</div>
        </div>
        <button
          class="absolute right-2 top-2 rounded-md p-1 text-foreground/50 opacity-0 transition-opacity hover:text-foreground focus:opacity-100 group-hover:opacity-100"
          @click="store.dismiss(toast.id)"
        >
          <X class="h-4 w-4" />
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(100%);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
