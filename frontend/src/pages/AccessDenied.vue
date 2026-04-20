<script setup lang="ts">
import { RouterLink } from "vue-router";
import { ShieldX } from "lucide-vue-next";
import Button from "@/components/ui/Button.vue";
</script>

<template>
  <div class="min-h-screen flex flex-col items-center justify-center text-center px-4">
    <ShieldX class="h-16 w-16 text-destructive mb-4" />
    <h1 class="text-2xl font-bold mb-2">Acceso Denegado</h1>
    <p class="text-muted-foreground mb-6 max-w-md">
      No tenés permisos para acceder a esta sección. Contactá al administrador si creés que esto es un error.
    </p>
    <div class="flex gap-3">
      <RouterLink to="/dashboard">
        <Button>Ir al Dashboard</Button>
      </RouterLink>
      <RouterLink to="/">
        <Button variant="outline">Volver al inicio</Button>
      </RouterLink>
    </div>
  </div>
</template>
