<script setup lang="ts">
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Badge from "@/components/ui/Badge.vue";
import Button from "@/components/ui/Button.vue";
import Table from "@/components/ui/Table.vue";
import TableHeader from "@/components/ui/TableHeader.vue";
import TableBody from "@/components/ui/TableBody.vue";
import TableRow from "@/components/ui/TableRow.vue";
import TableHead from "@/components/ui/TableHead.vue";
import TableCell from "@/components/ui/TableCell.vue";
import { CalendarDays, UserCheck, Send, Users } from "lucide-vue-next";

const staffStats = [
  { label: "Turnos Hoy", value: "32", icon: CalendarDays, color: "text-primary" },
  { label: "Pacientes en Espera", value: "5", icon: UserCheck, color: "text-orange-500" },
  { label: "Confirmaciones Pendientes", value: "8", icon: Send, color: "text-secondary" },
  { label: "Altas Hoy", value: "3", icon: Users, color: "text-violet-500" },
];

const turnosHoy = [
  { hora: "08:00", paciente: "Juan Pérez", estudio: "Ecografía Abdominal", estado: "Confirmado" },
  { hora: "09:30", paciente: "María García", estudio: "Rx Torácica", estado: "En espera" },
  { hora: "10:00", paciente: "Carlos López", estudio: "TC Cerebral", estado: "Pendiente" },
  { hora: "11:00", paciente: "Ana Martínez", estudio: "Mamografía", estado: "Confirmado" },
  { hora: "14:00", paciente: "Luis Torres", estudio: "Densitometría", estado: "Pendiente" },
];
</script>

<template>
  <div class="space-y-6">
    <div>
      <h1 class="text-2xl font-bold">Panel Operativo</h1>
      <p class="text-muted-foreground">Gestión de turnos, pacientes y agendas</p>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-4 gap-4">
      <Card v-for="s in staffStats" :key="s.label">
        <CardContent class="flex items-center gap-3 p-4">
          <component :is="s.icon" :class="['h-6 w-6', s.color]" />
          <div>
            <p class="text-xl font-bold">{{ s.value }}</p>
            <p class="text-xs text-muted-foreground">{{ s.label }}</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <CardTitle class="flex items-center gap-2 text-base">
          <CalendarDays class="h-4 w-4 text-primary" /> Turnos del Día
        </CardTitle>
        <Button size="sm">Nuevo Turno</Button>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Hora</TableHead>
              <TableHead>Paciente</TableHead>
              <TableHead>Estudio</TableHead>
              <TableHead>Estado</TableHead>
              <TableHead class="text-right">Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="(t, i) in turnosHoy" :key="i">
              <TableCell class="font-medium">{{ t.hora }}</TableCell>
              <TableCell>{{ t.paciente }}</TableCell>
              <TableCell>{{ t.estudio }}</TableCell>
              <TableCell>
                <Badge :variant="t.estado === 'Confirmado' ? 'default' : t.estado === 'En espera' ? 'secondary' : 'outline'">
                  {{ t.estado }}
                </Badge>
              </TableCell>
              <TableCell class="text-right space-x-1">
                <Button variant="ghost" size="sm">Confirmar</Button>
                <Button variant="ghost" size="sm" class="text-destructive">Cancelar</Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  </div>
</template>
