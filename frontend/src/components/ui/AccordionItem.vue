<script setup lang="ts">
import { inject, computed } from "vue";

const props = defineProps<{ value: string }>();
const accordion = inject<any>("accordion");
const isOpen = computed(() => accordion?.openItem.value === props.value);
</script>

<template>
  <div class="border-b">
    <slot :isOpen="isOpen" />
  </div>
</template>
