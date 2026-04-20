<script setup lang="ts">
import { ref } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { Menu, Activity, LogIn } from "lucide-vue-next";
import Button from "@/components/ui/Button.vue";
import Sheet from "@/components/ui/Sheet.vue";
import { useAuthStore } from "@/stores/auth";

const navLinks = [
  { to: "/", label: "Inicio" },
  { to: "/servicios", label: "Servicios" },
  { to: "/reservar", label: "Reservar Turno" },
  { to: "/portal", label: "Portal Paciente" },
  { to: "/contacto", label: "Contacto" },
];

const open = ref(false);
const route = useRoute();
const auth = useAuthStore();
</script>

<template>
  <header class="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
    <div class="container flex h-16 items-center justify-between">
      <RouterLink to="/" class="flex items-center gap-2">
        <Activity class="h-7 w-7 text-primary" />
        <span class="text-xl font-bold tracking-tight text-primary">ECOMED</span>
      </RouterLink>

      <!-- Desktop nav -->
      <nav class="hidden md:flex items-center gap-1">
        <RouterLink
          v-for="link in navLinks"
          :key="link.to"
          :to="link.to"
          :class="[
            'px-3 py-2 text-sm font-medium rounded-md transition-colors hover:bg-muted',
            route.path === link.to ? 'text-primary bg-muted' : 'text-muted-foreground'
          ]"
        >
          {{ link.label }}
        </RouterLink>
      </nav>

      <div class="hidden md:flex items-center gap-2">
        <RouterLink to="/reservar">
          <Button size="sm">Reservar Turno</Button>
        </RouterLink>
        <RouterLink v-if="auth.isAuthenticated" to="/dashboard">
          <Button variant="outline" size="sm">Mi Dashboard</Button>
        </RouterLink>
        <RouterLink v-else to="/login">
          <Button variant="outline" size="sm">
            <LogIn class="h-4 w-4 mr-1" /> Ingresar
          </Button>
        </RouterLink>
      </div>

      <!-- Mobile nav -->
      <Button variant="ghost" size="icon" class="md:hidden" @click="open = true">
        <Menu class="h-5 w-5" />
      </Button>

      <Sheet :open="open" side="right" @update:open="open = $event">
        <div class="w-72">
          <div class="flex items-center gap-2 mb-6">
            <Activity class="h-6 w-6 text-primary" />
            <span class="font-bold text-primary">ECOMED</span>
          </div>
          <nav class="flex flex-col gap-1">
            <RouterLink
              v-for="link in navLinks"
              :key="link.to"
              :to="link.to"
              :class="[
                'px-3 py-2.5 text-sm font-medium rounded-md transition-colors hover:bg-muted',
                route.path === link.to ? 'text-primary bg-muted' : 'text-muted-foreground'
              ]"
              @click="open = false"
            >
              {{ link.label }}
            </RouterLink>
            <div class="flex flex-col gap-2 mt-4 pt-4 border-t">
              <RouterLink to="/reservar" @click="open = false">
                <Button size="sm" class="w-full">Reservar Turno</Button>
              </RouterLink>
              <RouterLink v-if="auth.isAuthenticated" to="/dashboard" @click="open = false">
                <Button variant="outline" size="sm" class="w-full">Mi Dashboard</Button>
              </RouterLink>
              <RouterLink v-else to="/login" @click="open = false">
                <Button variant="outline" size="sm" class="w-full">
                  <LogIn class="h-4 w-4 mr-1" /> Ingresar
                </Button>
              </RouterLink>
            </div>
          </nav>
        </div>
      </Sheet>
    </div>
  </header>
</template>
