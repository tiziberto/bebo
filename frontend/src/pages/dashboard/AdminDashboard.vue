<script setup lang="ts">
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import { Users, CalendarDays, FileText, DollarSign, Activity, TrendingUp } from "lucide-vue-next";

const stats = [
  { label: "Pacientes Registrados", value: "2,847", icon: Users, color: "text-primary" },
  { label: "Turnos Hoy", value: "48", icon: CalendarDays, color: "text-secondary" },
  { label: "Estudios Pendientes", value: "12", icon: FileText, color: "text-orange-500" },
  { label: "Facturación Mes", value: "$1.250.000", icon: DollarSign, color: "text-emerald-600" },
];

const recentActivity = [
  { action: "Nuevo turno creado", detail: "Ecografía - Juan Pérez", time: "Hace 5 min" },
  { action: "Informe enviado", detail: "TC Cerebral - María López", time: "Hace 15 min" },
  { action: "Paciente registrado", detail: "Carlos Ruiz - DNI 40123456", time: "Hace 30 min" },
  { action: "Factura generada", detail: "OSDE - Lote #1234", time: "Hace 1 hora" },
  { action: "Turno cancelado", detail: "RMN Rodilla - Ana García", time: "Hace 2 horas" },
];

const monthSummary = [
  { label: "Estudios realizados", value: "324", change: "+12%" },
  { label: "Nuevos pacientes", value: "67", change: "+8%" },
  { label: "Tasa de ocupación", value: "87%", change: "+3%" },
  { label: "Informes entregados", value: "298", change: "+15%" },
];
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">Dashboard Administrativo</h1>
      <p class="text-muted-foreground">Vista general del sistema ECOMED</p>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <Card v-for="s in stats" :key="s.label">
        <CardContent class="flex items-center gap-4 p-5">
          <div class="p-2 rounded-lg bg-muted">
            <component :is="s.icon" :class="['h-6 w-6', s.color]" />
          </div>
          <div>
            <p class="text-2xl font-bold">{{ s.value }}</p>
            <p class="text-xs text-muted-foreground">{{ s.label }}</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <Card>
        <CardHeader>
          <CardTitle class="flex items-center gap-2 text-base">
            <Activity class="h-4 w-4 text-primary" /> Actividad Reciente
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-3">
            <div v-for="(a, i) in recentActivity" :key="i" class="flex items-start justify-between gap-2 py-2 border-b last:border-0">
              <div>
                <p class="text-sm font-medium">{{ a.action }}</p>
                <p class="text-xs text-muted-foreground">{{ a.detail }}</p>
              </div>
              <span class="text-xs text-muted-foreground whitespace-nowrap">{{ a.time }}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle class="flex items-center gap-2 text-base">
            <TrendingUp class="h-4 w-4 text-secondary" /> Resumen del Mes
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-4">
            <div v-for="item in monthSummary" :key="item.label" class="flex items-center justify-between">
              <span class="text-sm">{{ item.label }}</span>
              <div class="flex items-center gap-2">
                <span class="font-semibold">{{ item.value }}</span>
                <span class="text-xs text-secondary">{{ item.change }}</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>
