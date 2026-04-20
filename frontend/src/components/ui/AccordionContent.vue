<script setup lang="ts">
import { inject, computed } from "vue";

const props = defineProps<{ value: string }>();
const accordion = inject<any>("accordion");
const isOpen = computed(() => accordion?.openItem.value === props.value);
</script>

<template>
  <div v-if="isOpen" class="overflow-hidden text-sm transition-all animate-accordion-down pb-4 pt-0">
    <slot />
  </div>
</template>
