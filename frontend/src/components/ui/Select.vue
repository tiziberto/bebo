<script setup lang="ts">
import { cn } from "@/lib/utils";
import { ChevronDown } from "lucide-vue-next";

defineProps<{
  modelValue?: string;
  placeholder?: string;
  class?: string;
  options: { value: string; label: string }[];
}>();

defineEmits<{
  "update:modelValue": [value: string];
}>();
</script>

<template>
  <div class="relative">
    <select
      :value="modelValue"
      :class="cn('flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 appearance-none pr-8', $props.class)"
      @change="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
    >
      <option value="" disabled selected hidden>{{ placeholder || 'Seleccione...' }}</option>
      <option v-for="opt in options" :key="opt.value" :value="opt.value">
        {{ opt.label }}
      </option>
    </select>
    <ChevronDown class="absolute right-3 top-3 h-4 w-4 opacity-50 pointer-events-none" />
  </div>
</template>
