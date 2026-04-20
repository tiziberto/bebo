<script setup lang="ts">
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Button from "@/components/ui/Button.vue";
import Badge from "@/components/ui/Badge.vue";
import Table from "@/components/ui/Table.vue";
import TableHeader from "@/components/ui/TableHeader.vue";
import TableBody from "@/components/ui/TableBody.vue";
import TableRow from "@/components/ui/TableRow.vue";
import TableHead from "@/components/ui/TableHead.vue";
import TableCell from "@/components/ui/TableCell.vue";
import { Calendar, Download, FileText, Plus, User } from "lucide-vue-next";
import { useAuthStore } from "@/stores/auth";

const auth = useAuthStore();

const upcomingTurnos = [
  { id: 1, fecha: "15/02/2026", hora: "09:30", estudio: "Ecografía Abdominal", estado: "Confirmado" },
  { id: 2, fecha: "22/02/2026", hora: "14:00", estudio: "Resonancia de Rodilla", estado: "Pendiente" },
];

const historial = [
  { id: 1, fecha: "10/01/2026", estudio: "Radiografía Torácica", informe: true },
  { id: 2, fecha: "05/12/2025", estudio: "Tomografía Cerebral", informe: true },
  { id: 3, fecha: "20/10/2025", estudio: "Ecografía Tiroides", informe: true },
  { id: 4, fecha: "15/08/2025", estudio: "Mamografía", informe: false },
];
</script>

<template>
  <div class="space-y-6 max-w-4xl">
    <div class="flex items-start justify-between">
      <div>
        <h1 class="text-2xl font-bold">¡Hola, {{ auth.username }}!</h1>
        <p class="text-muted-foreground">Gestioná tus turnos y estudios desde aquí</p>
      </div>
      <Button size="sm">
        <Plus class="h-4 w-4 mr-1" /> Solicitar Turno
      </Button>
    </div>

    <!-- Próximos turnos -->
    <Card>
      <CardHeader>
        <CardTitle class="flex items-center gap-2 text-base">
          <Calendar class="h-4 w-4 text-primary" /> Próximos Turnos
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <Card v-for="t in upcomingTurnos" :key="t.id" class="bg-muted/30">
            <CardContent class="p-4 space-y-2">
              <p class="font-semibold">{{ t.estudio }}</p>
              <p class="text-sm text-muted-foreground">{{ t.fecha }} — {{ t.hora }}</p>
              <div class="flex items-center justify-between">
                <Badge :variant="t.estado === 'Confirmado' ? 'default' : 'secondary'">{{ t.estado }}</Badge>
                <Button variant="ghost" size="sm" class="text-destructive text-xs">Cancelar</Button>
              </div>
            </CardContent>
          </Card>
        </div>
      </CardContent>
    </Card>

    <!-- Historial -->
    <Card>
      <CardHeader>
        <CardTitle class="flex items-center gap-2 text-base">
          <FileText class="h-4 w-4 text-primary" /> Historial de Estudios
        </CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Fecha</TableHead>
              <TableHead>Estudio</TableHead>
              <TableHead class="text-right">Informe</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="h in historial" :key="h.id">
              <TableCell class="text-sm">{{ h.fecha }}</TableCell>
              <TableCell class="text-sm">{{ h.estudio }}</TableCell>
              <TableCell class="text-right">
                <Button v-if="h.informe" variant="ghost" size="sm">
                  <Download class="h-4 w-4 mr-1" /> Descargar
                </Button>
                <span v-else class="text-xs text-muted-foreground">No disponible</span>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <!-- Datos personales -->
    <Card>
      <CardHeader>
        <CardTitle class="flex items-center gap-2 text-base">
          <User class="h-4 w-4 text-primary" /> Mis Datos
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
          <div><span class="text-muted-foreground">Usuario:</span> <span class="font-medium">{{ auth.username }}</span></div>
          <div><span class="text-muted-foreground">Obra Social:</span> <span class="font-medium">OSDE 310</span></div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>
