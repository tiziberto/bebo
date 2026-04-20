<script setup lang="ts">
import { watch } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const router = useRouter();

watch(
  () => auth.loading,
  (loading) => {
    if (!loading && !auth.isAuthenticated) {
      router.replace("/login");
    }
  },
  { immediate: true }
);
</script>

<template>
  <div v-if="auth.loading" class="flex items-center justify-center min-h-screen">
    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
  </div>
  <slot v-else-if="auth.isAuthenticated" />
</template>
