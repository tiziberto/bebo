<script setup lang="ts">
import { inject, computed } from "vue";
import { ChevronDown } from "lucide-vue-next";

const props = defineProps<{ value: string; class?: string }>();
const accordion = inject<any>("accordion");
const isOpen = computed(() => accordion?.openItem.value === props.value);

function toggle() {
  accordion?.toggle(props.value);
}
</script>

<template>
  <h3 class="flex">
    <button
      type="button"
      :class="['flex flex-1 items-center justify-between py-4 font-medium transition-all hover:underline', $props.class]"
      @click="toggle"
    >
      <slot />
      <ChevronDown
        class="h-4 w-4 shrink-0 transition-transform duration-200"
        :class="{ 'rotate-180': isOpen }"
      />
    </button>
  </h3>
</template>
