<script setup lang="ts">
import { ChevronLeft, ChevronRight } from "lucide-vue-next";

const props = defineProps<{
  page: number;          // 0-based current page
  totalPages: number;
  totalElements: number;
  size: number;
  loading?: boolean;
}>();

const emit = defineEmits<{
  (e: "change", page: number): void;
}>();

function prev() { if (props.page > 0) emit("change", props.page - 1); }
function next() { if (props.page < props.totalPages - 1) emit("change", props.page + 1); }

function displayRange(): string {
  if (props.totalElements === 0) return "0";
  const from = props.page * props.size + 1;
  const to = Math.min((props.page + 1) * props.size, props.totalElements);
  return `${from}–${to} de ${props.totalElements}`;
}
</script>

<template>
  <div class="flex items-center justify-between px-4 py-3 border-t text-sm">
    <span class="text-muted-foreground text-xs">{{ displayRange() }}</span>
    <div class="flex items-center gap-1">
      <button
        @click="prev"
        :disabled="page === 0 || loading"
        class="inline-flex items-center justify-center h-7 w-7 rounded border hover:bg-muted transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <ChevronLeft class="h-3.5 w-3.5" />
      </button>
      <span class="px-2 text-xs text-muted-foreground">
        Pág. {{ page + 1 }} / {{ Math.max(totalPages, 1) }}
      </span>
      <button
        @click="next"
        :disabled="page >= totalPages - 1 || loading"
        class="inline-flex items-center justify-center h-7 w-7 rounded border hover:bg-muted transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
      >
        <ChevronRight class="h-3.5 w-3.5" />
      </button>
    </div>
  </div>
</template>
