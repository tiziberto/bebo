<script setup lang="ts">
import { RouterLink } from "vue-router";
import { Activity, Phone, Mail, MapPin, Clock } from "lucide-vue-next";

const navLinks = [
  { to: "/", label: "Inicio" },
  { to: "/servicios", label: "Servicios" },
  { to: "/reservar", label: "Reservar Turno" },
  { to: "/portal", label: "Portal Paciente" },
  { to: "/contacto", label: "Contacto" },
];
</script>

<template>
  <footer class="bg-foreground text-primary-foreground">
    <div class="container py-12">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- Brand -->
        <div class="space-y-3">
          <div class="flex items-center gap-2">
            <Activity class="h-6 w-6" />
            <span class="text-lg font-bold">ECOMED</span>
          </div>
          <p class="text-sm opacity-80">
            Clínica de Imágenes Diagnósticas. Tecnología de última generación al servicio de tu salud.
          </p>
        </div>

        <!-- Links -->
        <div class="space-y-3">
          <h4 class="font-semibold text-sm uppercase tracking-wider opacity-70">Navegación</h4>
          <nav class="flex flex-col gap-2">
            <RouterLink
              v-for="l in navLinks"
              :key="l.to"
              :to="l.to"
              class="text-sm opacity-80 hover:opacity-100 transition-opacity"
            >
              {{ l.label }}
            </RouterLink>
          </nav>
        </div>

        <!-- Contact -->
        <div class="space-y-3">
          <h4 class="font-semibold text-sm uppercase tracking-wider opacity-70">Contacto</h4>
          <div class="space-y-2 text-sm opacity-80">
            <p class="flex items-center gap-2"><MapPin class="h-4 w-4 shrink-0" /> Córdoba 180 Q8300, Neuquén</p>
            <p class="flex items-center gap-2"><Phone class="h-4 w-4 shrink-0" /> 0299 456-8800</p>
            <p class="flex items-center gap-2"><Mail class="h-4 w-4 shrink-0" /> info@ecomed.com.ar</p>
          </div>
        </div>

        <!-- Hours -->
        <div class="space-y-3">
          <h4 class="font-semibold text-sm uppercase tracking-wider opacity-70">Horarios</h4>
          <div class="space-y-2 text-sm opacity-80">
            <p class="flex items-center gap-2"><Clock class="h-4 w-4 shrink-0" /> Lunes a Viernes: 7:00 - 20:00</p>
            <p class="flex items-center gap-2"><Clock class="h-4 w-4 shrink-0" /> Sábados: 8:00 - 14:00</p>
          </div>
        </div>
      </div>

      <div class="mt-10 pt-6 border-t border-primary-foreground/20 text-center text-sm opacity-60">
        &copy; {{ new Date().getFullYear() }} ECOMED. Todos los derechos reservados.
      </div>
    </div>
  </footer>
</template>
