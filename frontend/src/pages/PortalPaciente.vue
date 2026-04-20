<script setup lang="ts">
import { ref } from "vue";
import Button from "@/components/ui/Button.vue";
import Input from "@/components/ui/Input.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import CardDescription from "@/components/ui/CardDescription.vue";
import Table from "@/components/ui/Table.vue";
import TableHeader from "@/components/ui/TableHeader.vue";
import TableBody from "@/components/ui/TableBody.vue";
import TableRow from "@/components/ui/TableRow.vue";
import TableHead from "@/components/ui/TableHead.vue";
import TableCell from "@/components/ui/TableCell.vue";
import Badge from "@/components/ui/Badge.vue";
import { Download, LogOut, Calendar, FileText } from "lucide-vue-next";

const mockTurnos = [
  { id: 1, fecha: "15/02/2026", estudio: "Ecografía Abdominal", estado: "Confirmado" },
  { id: 2, fecha: "22/02/2026", estudio: "Resonancia de Rodilla", estado: "Pendiente" },
];

const mockHistorial = [
  { id: 1, fecha: "10/01/2026", estudio: "Radiografía Torácica", informe: true },
  { id: 2, fecha: "05/12/2025", estudio: "Tomografía Cerebral", informe: true },
  { id: 3, fecha: "20/10/2025", estudio: "Ecografía Tiroides", informe: true },
  { id: 4, fecha: "15/08/2025", estudio: "Mamografía", informe: false },
];

const loggedIn = ref(false);
const dni = ref("");
const password = ref("");
</script>

<template>
  <!-- Login -->
  <template v-if="!loggedIn">
    <section class="bg-muted/50 py-12 md:py-16">
      <div class="container text-center max-w-2xl">
        <h1 class="text-3xl md:text-4xl font-bold mb-3">Portal del Paciente</h1>
        <p class="text-muted-foreground">Acceda a sus turnos, historial de estudios e informes médicos.</p>
      </div>
    </section>
    <section class="py-12 md:py-20">
      <div class="container max-w-sm">
        <Card>
          <CardHeader>
            <CardTitle>Iniciar Sesión</CardTitle>
            <CardDescription>Ingrese con su DNI y contraseña</CardDescription>
          </CardHeader>
          <CardContent>
            <form @submit.prevent="loggedIn = true" class="space-y-4">
              <div class="space-y-2">
                <label class="text-sm font-medium">DNI</label>
                <Input v-model="dni" placeholder="12345678" required />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium">Contraseña</label>
                <Input v-model="password" type="password" placeholder="••••••••" required />
              </div>
              <Button type="submit" class="w-full">Ingresar</Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </section>
  </template>

  <!-- Portal -->
  <template v-else>
    <section class="bg-muted/50 py-8">
      <div class="container flex items-center justify-between">
        <div>
          <h1 class="text-2xl font-bold">Bienvenido/a</h1>
          <p class="text-sm text-muted-foreground">DNI: {{ dni || '12345678' }}</p>
        </div>
        <Button variant="outline" size="sm" @click="loggedIn = false">
          <LogOut class="h-4 w-4 mr-1" /> Cerrar sesión
        </Button>
      </div>
    </section>

    <section class="py-10">
      <div class="container space-y-8">
        <!-- Próximos turnos -->
        <Card>
          <CardHeader>
            <CardTitle class="flex items-center gap-2 text-lg">
              <Calendar class="h-5 w-5 text-primary" /> Próximos Turnos
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Card v-for="t in mockTurnos" :key="t.id" class="bg-muted/30">
                <CardContent class="p-4 space-y-1">
                  <p class="font-semibold">{{ t.estudio }}</p>
                  <p class="text-sm text-muted-foreground">{{ t.fecha }}</p>
                  <Badge :variant="t.estado === 'Confirmado' ? 'default' : 'secondary'">
                    {{ t.estado }}
                  </Badge>
                </CardContent>
              </Card>
            </div>
          </CardContent>
        </Card>

        <!-- Historial -->
        <Card>
          <CardHeader>
            <CardTitle class="flex items-center gap-2 text-lg">
              <FileText class="h-5 w-5 text-primary" /> Historial de Estudios
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
                <TableRow v-for="h in mockHistorial" :key="h.id">
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
      </div>
    </section>
  </template>
</template>
