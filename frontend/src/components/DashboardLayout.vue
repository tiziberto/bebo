<script setup lang="ts">
import { ref } from "vue";
import { PanelLeft } from "lucide-vue-next";
import DashboardSidebar from "./DashboardSidebar.vue";
import Button from "@/components/ui/Button.vue";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();
const collapsed = ref(false);
</script>

<template>
  <div class="min-h-screen flex w-full">
    <DashboardSidebar :collapsed="collapsed" @update:collapsed="collapsed = $event" />
    <div class="flex-1 flex flex-col">
      <header class="sticky top-0 z-40 flex h-14 items-center gap-4 border-b bg-background/95 backdrop-blur px-4">
        <Button variant="ghost" size="icon" @click="collapsed = !collapsed">
          <PanelLeft class="h-5 w-5" />
        </Button>
        <div class="flex-1" />
        <span class="text-xs text-muted-foreground">
          {{ auth.isAuthenticated ? auth.username : '' }}
        </span>
      </header>
      <main class="flex-1 p-4 md:p-6">
        <RouterView />
      </main>
    </div>
  </div>
</template>
