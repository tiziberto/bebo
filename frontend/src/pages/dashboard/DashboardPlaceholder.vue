<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import { Settings } from "lucide-vue-next";

const route = useRoute();

const section = computed(() => {
  const s = route.path.split("/").pop()?.replace(/-/g, " ") || "Sección";
  return s.charAt(0).toUpperCase() + s.slice(1);
});
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold capitalize">{{ section }}</h1>
      <p class="text-muted-foreground">Esta sección está en desarrollo</p>
    </div>
    <Card>
      <CardContent class="flex flex-col items-center justify-center py-16 text-center">
        <Settings class="h-12 w-12 text-muted-foreground/40 mb-4" />
        <p class="text-lg font-medium mb-1">Módulo en construcción</p>
        <p class="text-sm text-muted-foreground max-w-md">
          La funcionalidad de <strong class="capitalize">{{ section }}</strong> estará disponible próximamente.
          Este módulo se integrará con el backend HIS del sistema.
        </p>
      </CardContent>
    </Card>
  </div>
</template>
