<script setup lang="ts">
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Button from "@/components/ui/Button.vue";
import Table from "@/components/ui/Table.vue";
import TableHeader from "@/components/ui/TableHeader.vue";
import TableBody from "@/components/ui/TableBody.vue";
import TableRow from "@/components/ui/TableRow.vue";
import TableHead from "@/components/ui/TableHead.vue";
import TableCell from "@/components/ui/TableCell.vue";
import { DollarSign, FileSpreadsheet, BarChart3, Download } from "lucide-vue-next";

const accStats = [
  { label: "Facturación Mes", value: "$1.300.000", icon: DollarSign, color: "text-emerald-600" },
  { label: "Rendiciones Pendientes", value: "4", icon: FileSpreadsheet, color: "text-orange-500" },
  { label: "Liquidaciones", value: "12", icon: BarChart3, color: "text-primary" },
];

const facturacionMes = [
  { obraSocial: "OSDE", monto: "$420.000", estado: "Facturado" },
  { obraSocial: "Swiss Medical", monto: "$310.000", estado: "Pendiente" },
  { obraSocial: "Galeno", monto: "$185.000", estado: "Facturado" },
  { obraSocial: "PAMI", monto: "$290.000", estado: "En proceso" },
  { obraSocial: "Particular", monto: "$95.000", estado: "Facturado" },
];

const resumenMensual = [
  { label: "Total facturado", value: "$1.300.000" },
  { label: "Total cobrado", value: "$985.000" },
  { label: "Pendiente de cobro", value: "$315.000" },
  { label: "Honorarios médicos", value: "$520.000" },
];

const rendiciones = [
  { periodo: "Enero 2026", os: "OSDE", monto: "$380.000" },
  { periodo: "Enero 2026", os: "Swiss Medical", monto: "$290.000" },
  { periodo: "Diciembre 2025", os: "Galeno", monto: "$175.000" },
];
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-start justify-between">
      <div>
        <h1 class="text-2xl font-bold">Panel de Contaduría</h1>
        <p class="text-muted-foreground">Gestión financiera y facturación</p>
      </div>
      <Button size="sm" variant="outline">
        <Download class="h-4 w-4 mr-1" /> Exportar
      </Button>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
      <Card v-for="s in accStats" :key="s.label">
        <CardContent class="flex items-center gap-3 p-4">
          <component :is="s.icon" :class="['h-8 w-8', s.color]" />
          <div>
            <p class="text-xl font-bold">{{ s.value }}</p>
            <p class="text-xs text-muted-foreground">{{ s.label }}</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <Card>
      <CardHeader>
        <CardTitle class="flex items-center gap-2 text-base">
          <DollarSign class="h-4 w-4 text-primary" /> Facturación por Obra Social — Febrero 2026
        </CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Obra Social</TableHead>
              <TableHead>Monto</TableHead>
              <TableHead>Estado</TableHead>
              <TableHead class="text-right">Acciones</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-for="(f, i) in facturacionMes" :key="i">
              <TableCell class="font-medium">{{ f.obraSocial }}</TableCell>
              <TableCell>{{ f.monto }}</TableCell>
              <TableCell>
                <span :class="[
                  'text-xs font-medium px-2 py-1 rounded-full',
                  f.estado === 'Facturado' ? 'bg-emerald-100 text-emerald-700' :
                  f.estado === 'Pendiente' ? 'bg-orange-100 text-orange-700' :
                  'bg-blue-100 text-blue-700'
                ]">
                  {{ f.estado }}
                </span>
              </TableCell>
              <TableCell class="text-right">
                <Button variant="ghost" size="sm">Ver detalle</Button>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </CardContent>
    </Card>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <Card>
        <CardHeader>
          <CardTitle class="text-base">Resumen Mensual</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-3">
            <div v-for="item in resumenMensual" :key="item.label" class="flex justify-between text-sm border-b pb-2 last:border-0">
              <span class="text-muted-foreground">{{ item.label }}</span>
              <span class="font-semibold">{{ item.value }}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle class="text-base">Últimas Rendiciones</CardTitle>
        </CardHeader>
        <CardContent>
          <div class="space-y-3">
            <div v-for="(r, i) in rendiciones" :key="i" class="flex items-center justify-between p-2 bg-muted/30 rounded">
              <div>
                <p class="text-sm font-medium">{{ r.os }}</p>
                <p class="text-xs text-muted-foreground">{{ r.periodo }}</p>
              </div>
              <span class="text-sm font-semibold">{{ r.monto }}</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>
