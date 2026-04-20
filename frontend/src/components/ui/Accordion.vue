<script setup lang="ts">
import { ref, provide } from "vue";

defineProps<{ class?: string }>();

const openItem = ref<string | null>(null);

function toggle(value: string) {
  openItem.value = openItem.value === value ? null : value;
}

provide("accordion", { openItem, toggle });
</script>

<template>
  <div :class="$props.class">
    <slot />
  </div>
</template>
