<script setup lang="ts">
import { cn } from "@/lib/utils";
defineProps<{ class?: string }>();
</script>

<template>
  <span :class="cn('flex h-full w-full items-center justify-center rounded-full bg-muted', $props.class)">
    <slot />
  </span>
</template>
