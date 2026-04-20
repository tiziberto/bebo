<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";
import Select from "@/components/ui/Select.vue";
import { Plus, X, CalendarDays, Clock, Search, RefreshCw, Mail, CheckCircle2 } from "lucide-vue-next";

const API = "http://localhost:8080/api";
const auth = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

interface TurnoDto {
  id: number; pacienteId: number; pacienteNombre: string; pacienteDni: string;
  profesionalId: number; profesionalNombre: string;
  sucursalId: number; sucursalNombre: string;
  estudioId: number | null; estudioNombre: string | null;
  obraSocialId: number | null; obraSocialNombre: string | null;
  planId: number | null; planNombre: string | null;
  fechaHora: string; estado: string; observaciones: string | null; createdAt: string;
}

const turnos = ref<TurnoDto[]>([]);
const loading = ref(true);
const globalError = ref("");

// Filters
const selectedDate = ref(new Date().toISOString().split("T")[0]);
const filterProfId = ref("");
const filterSucId = ref("");
const filterEstado = ref("");

// Lookups
const profesionales = ref<{ id: number; nombre: string; apellido: string }[]>([]);
const sucursales = ref<{ id: number; nombre: string }[]>([]);
const estudios = ref<{ id: number; nombre: string }[]>([]);
const obrasSociales = ref<{ id: number; nombre: string; planes?: { id: number; nombre: string }[] }[]>([]);
const pacientes = ref<{ id: number; nombre: string; apellido: string; dni: string }[]>([]);

// New turno
const showNewForm = ref(false);
const newForm = ref({ pacienteId: "", profesionalId: "", sucursalId: "", estudioId: "", obraSocialId: "", planId: "", fechaHora: "", observaciones: "" });
const newSaving = ref(false);
const newError = ref("");
const pacienteSearch = ref("");
const pacienteResults = ref<{ id: number; nombre: string; apellido: string; dni: string }[]>([]);

// Reprogramar
const reprogramId = ref<number | null>(null);
const reprogramFecha = ref("");

const estadoOptions = [
  { value: "", label: "Todos" },
  { value: "PENDIENTE",  label: "Pendiente" },
  { value: "CONFIRMADO", label: "Confirmado" },
  { value: "EN_ESPERA",  label: "En Espera" },
  { value: "EN_CURSO",   label: "En Curso" },
  { value: "ATENDIDO",   label: "Atendido" },
  { value: "AUSENTE",    label: "Ausente" },
  { value: "CANCELADO",  label: "Cancelado" },
];

const estadoBadge: Record<string, string> = {
  PENDIENTE:  "bg-yellow-100 text-yellow-800",
  CONFIRMADO: "bg-blue-100 text-blue-800",
  EN_ESPERA:  "bg-orange-100 text-orange-800",
  EN_CURSO:   "bg-purple-100 text-purple-800",
  ATENDIDO:   "bg-green-100 text-green-800",
  AUSENTE:    "bg-gray-100 text-gray-600",
  CANCELADO:  "bg-red-100 text-red-700",
};

const profOptions = computed(() => [
  { value: "", label: "Todos los profesionales" },
  ...profesionales.value.map(p => ({ value: String(p.id), label: p.apellido ? `${p.apellido}, ${p.nombre}` : p.nombre })),
]);

const sucOptions = computed(() => [
  { value: "", label: "Todas las sucursales" },
  ...sucursales.value.map(s => ({ value: String(s.id), label: s.nombre })),
]);

const estudioOptions = computed(() => [
  { value: "", label: "Sin estudio" },
  ...estudios.value.map(e => ({ value: String(e.id), label: e.nombre })),
]);

const osOptions = computed(() => [
  { value: "", label: "Sin O.S." },
  ...obrasSociales.value.map(o => ({ value: String(o.id), label: o.nombre })),
]);

function planesForOs(osId: string) {
  if (!osId) return [];
  const os = obrasSociales.value.find(o => o.id === Number(osId));
  return (os?.planes ?? []).map(p => ({ value: String(p.id), label: p.nombre }));
}

const filteredTurnos = computed(() => {
  let list = turnos.value;
  if (filterEstado.value) list = list.filter(t => t.estado === filterEstado.value);
  return list;
});

const turnoStats = computed(() => {
  const total = turnos.value.length;
  const pendientes = turnos.value.filter(t => t.estado === "PENDIENTE").length;
  const confirmados = turnos.value.filter(t => t.estado === "CONFIRMADO").length;
  const completados = turnos.value.filter(t => t.estado === "COMPLETADO").length;
  return { total, pendientes, confirmados, completados };
});

async function loadLookups() {
  try {
    const [profRes, sucRes, estRes, osRes] = await Promise.all([
      axios.get(`${API}/profesionales`, { headers: headers() }),
      axios.get(`${API}/sucursales`, { headers: headers() }),
      axios.get(`${API}/estudios`, { headers: headers() }),
      axios.get(`${API}/obras-sociales`, { headers: headers() }),
    ]);
    profesionales.value = profRes.data;
    sucursales.value = sucRes.data;
    estudios.value = estRes.data;
    obrasSociales.value = osRes.data;
  } catch { /* silent */ }
}

async function loadTurnos() {
  loading.value = true;
  globalError.value = "";
  try {
    let url = `${API}/turnos?fecha=${selectedDate.value}`;
    if (filterProfId.value) url += `&profesionalId=${filterProfId.value}`;
    if (filterSucId.value) url += `&sucursalId=${filterSucId.value}`;
    const res = await axios.get(url, { headers: headers() });
    turnos.value = res.data;
  } catch {
    globalError.value = "Error al cargar turnos.";
  } finally { loading.value = false; }
}

onMounted(async () => {
  await loadLookups();
  await loadTurnos();
});

watch([selectedDate, filterProfId, filterSucId], loadTurnos);

const pacienteSearched = ref(false);   // si ya buscó y no encontró nada

async function searchPacientes() {
  pacienteSearched.value = false;
  if (pacienteSearch.value.length < 2) { pacienteResults.value = []; return; }
  try {
    const res = await axios.get(`${API}/pacientes?q=${encodeURIComponent(pacienteSearch.value)}`, { headers: headers() });
    pacienteResults.value = res.data.slice(0, 8);
    pacienteSearched.value = true;
  } catch { /* silent */ }
}

function selectPaciente(p: { id: number; nombre: string; apellido: string; dni: string }) {
  newForm.value.pacienteId = String(p.id);
  pacienteSearch.value = `${p.apellido ? p.apellido + ', ' : ''}${p.nombre} - DNI ${p.dni}`;
  pacienteResults.value = [];
  pacienteSearched.value = false;
}

// ── Modal alta rápida de paciente ─────────────────────────────────────────────
const showQuickPaciente = ref(false);
const quickForm = ref({
  nombre: "", apellido: "", dni: "", fechaNacimiento: "",
  sexo: "", telefono: "", email: "", obraSocialId: "", planId: "",
});
const quickSaving = ref(false);
const quickError  = ref("");

function openQuickPaciente() {
  // Pre-rellenar nombre/apellido desde lo que escribió
  const parts = pacienteSearch.value.trim().split(/\s+/);
  quickForm.value = {
    nombre: parts.slice(1).join(" ") || "",
    apellido: parts[0] || "",
    dni: /^\d+$/.test(pacienteSearch.value.trim()) ? pacienteSearch.value.trim() : "",
    fechaNacimiento: "", sexo: "", telefono: "", email: "",
    obraSocialId: "", planId: "",
  };
  quickError.value = "";
  showQuickPaciente.value = true;
}

async function saveQuickPaciente() {
  quickError.value = "";
  if (!quickForm.value.nombre || !quickForm.value.apellido || !quickForm.value.dni) {
    quickError.value = "Nombre, apellido y DNI son obligatorios."; return;
  }
  quickSaving.value = true;
  try {
    const res = await axios.post(`${API}/pacientes`, {
      nombre:           quickForm.value.nombre,
      apellido:         quickForm.value.apellido,
      dni:              quickForm.value.dni,
      fechaNacimiento:  quickForm.value.fechaNacimiento || null,
      sexo:             quickForm.value.sexo || null,
      telefono:         quickForm.value.telefono || null,
      email:            quickForm.value.email || null,
      obraSocialId:     quickForm.value.obraSocialId ? Number(quickForm.value.obraSocialId) : null,
      planId:           quickForm.value.planId ? Number(quickForm.value.planId) : null,
    }, { headers: headers() });

    // Auto-seleccionar el paciente recién creado
    selectPaciente({
      id: res.data.id,
      nombre: res.data.nombre,
      apellido: res.data.apellido,
      dni: res.data.dni,
    });
    showQuickPaciente.value = false;
  } catch (e: any) {
    quickError.value = e?.response?.data?.error ?? "Error al crear paciente.";
  } finally {
    quickSaving.value = false;
  }
}

async function createTurno() {
  newError.value = "";
  if (!newForm.value.pacienteId || !newForm.value.profesionalId || !newForm.value.sucursalId || !newForm.value.fechaHora) {
    newError.value = "Paciente, profesional, sucursal y fecha/hora son obligatorios."; return;
  }
  newSaving.value = true;
  try {
    await axios.post(`${API}/turnos`, {
      pacienteId: Number(newForm.value.pacienteId),
      profesionalId: Number(newForm.value.profesionalId),
      sucursalId: Number(newForm.value.sucursalId),
      estudioId: newForm.value.estudioId ? Number(newForm.value.estudioId) : null,
      obraSocialId: newForm.value.obraSocialId ? Number(newForm.value.obraSocialId) : null,
      planId: newForm.value.planId ? Number(newForm.value.planId) : null,
      fechaHora: newForm.value.fechaHora,
      observaciones: newForm.value.observaciones || null,
    }, { headers: headers() });
    showNewForm.value = false;
    newForm.value = { pacienteId: "", profesionalId: "", sucursalId: "", estudioId: "", obraSocialId: "", planId: "", fechaHora: "", observaciones: "" };
    pacienteSearch.value = "";
    await loadTurnos();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear turno.";
  } finally { newSaving.value = false; }
}

async function cambiarEstado(id: number, estado: string) {
  try {
    await axios.put(`${API}/turnos/${id}/estado`, { estado }, { headers: headers() });
    await loadTurnos();
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al cambiar estado.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  }
}

// ── Envío de confirmación por email ──────────────────────────────────────────
const enviandoEmailId = ref<number | null>(null);
const emailToast = ref<{ msg: string; ok: boolean } | null>(null);

async function enviarConfirmacion(turno: TurnoDto) {
  if (!turno.pacienteEmail) {
    emailToast.value = { msg: `${turno.pacienteApellido} no tiene email registrado`, ok: false };
    setTimeout(() => { emailToast.value = null; }, 4000);
    return;
  }
  enviandoEmailId.value = turno.id;
  try {
    await axios.post(`${API}/turnos/${turno.id}/enviar-confirmacion`, {}, { headers: headers() });
    emailToast.value = { msg: `Email enviado a ${turno.pacienteEmail}`, ok: true };
    await loadTurnos();
  } catch (e: any) {
    emailToast.value = { msg: e?.response?.data?.error ?? "Error al enviar email", ok: false };
  } finally {
    enviandoEmailId.value = null;
    setTimeout(() => { emailToast.value = null; }, 5000);
  }
}

async function cancelarTurno(id: number) {
  try {
    await axios.put(`${API}/turnos/${id}/cancelar`, {}, { headers: headers() });
    await loadTurnos();
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al cancelar.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  }
}

function startReprogramar(t: TurnoDto) {
  reprogramId.value = t.id;
  reprogramFecha.value = t.fechaHora.substring(0, 16);
}

async function doReprogramar() {
  if (!reprogramId.value || !reprogramFecha.value) return;
  try {
    await axios.put(`${API}/turnos/${reprogramId.value}/reprogramar`, { fechaHora: reprogramFecha.value }, { headers: headers() });
    reprogramId.value = null;
    await loadTurnos();
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al reprogramar.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  }
}

function formatTime(dt: string) {
  return dt.substring(11, 16);
}

function formatDate(dt: string) {
  const d = new Date(dt);
  return d.toLocaleDateString("es-AR", { weekday: "long", day: "2-digit", month: "long", year: "numeric" });
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">Gestión de Turnos</h1>
        <p class="text-muted-foreground text-sm">{{ formatDate(selectedDate) }}</p>
      </div>
      <button @click="showNewForm = !showNewForm" class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors">
        <component :is="showNewForm ? X : Plus" class="h-4 w-4" />
        {{ showNewForm ? "Cancelar" : "Nuevo Turno" }}
      </button>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
      <Card><CardContent class="p-4 text-center"><p class="text-2xl font-bold">{{ turnoStats.total }}</p><p class="text-xs text-muted-foreground">Total</p></CardContent></Card>
      <Card><CardContent class="p-4 text-center"><p class="text-2xl font-bold text-yellow-600">{{ turnoStats.pendientes }}</p><p class="text-xs text-muted-foreground">Pendientes</p></CardContent></Card>
      <Card><CardContent class="p-4 text-center"><p class="text-2xl font-bold text-blue-600">{{ turnoStats.confirmados }}</p><p class="text-xs text-muted-foreground">Confirmados</p></CardContent></Card>
      <Card><CardContent class="p-4 text-center"><p class="text-2xl font-bold text-emerald-600">{{ turnoStats.completados }}</p><p class="text-xs text-muted-foreground">Completados</p></CardContent></Card>
    </div>

    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ globalError }}</div>

    <!-- Filters -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
      <Input v-model="selectedDate" type="date" />
      <Select v-model="filterProfId" :options="profOptions" />
      <Select v-model="filterSucId" :options="sucOptions" />
      <Select v-model="filterEstado" :options="estadoOptions" />
    </div>

    <!-- New Turno Form -->
    <Card v-if="showNewForm">
      <CardHeader><CardTitle class="text-base">Asignar nuevo turno</CardTitle></CardHeader>
      <CardContent>
        <form @submit.prevent="createTurno" class="space-y-4">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <!-- Patient search -->
            <div class="space-y-1 relative">
              <label class="text-sm font-medium">Paciente <span class="text-destructive">*</span></label>
              <div class="relative">
                <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                <Input v-model="pacienteSearch" placeholder="Buscar por nombre o DNI..."
                  class="pl-9" @input="searchPacientes" autocomplete="off" />
              </div>
              <!-- Resultados encontrados -->
              <div v-if="pacienteResults.length > 0"
                class="absolute z-20 w-full bg-background border rounded-md shadow-lg mt-1 max-h-48 overflow-y-auto">
                <button v-for="p in pacienteResults" :key="p.id" type="button" @click="selectPaciente(p)"
                  class="w-full text-left px-3 py-2 text-sm hover:bg-muted transition-colors border-b last:border-0">
                  <span class="font-medium">{{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}</span>
                  <span class="text-muted-foreground text-xs ml-2">DNI {{ p.dni }}</span>
                </button>
              </div>
              <!-- Sin resultados → opción de crear -->
              <div v-else-if="pacienteSearched && pacienteSearch.length >= 2 && !newForm.pacienteId"
                class="absolute z-20 w-full bg-background border rounded-md shadow-lg mt-1">
                <div class="px-3 py-2 text-sm text-muted-foreground border-b">
                  No se encontró "<span class="font-medium">{{ pacienteSearch }}</span>"
                </div>
                <button type="button" @click="openQuickPaciente"
                  class="w-full text-left px-3 py-2.5 text-sm font-medium text-primary hover:bg-primary/5 transition-colors flex items-center gap-2">
                  <Plus class="h-4 w-4" />
                  Crear nuevo paciente "{{ pacienteSearch }}"
                </button>
              </div>
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Profesional <span class="text-destructive">*</span></label>
              <Select v-model="newForm.profesionalId" :options="profOptions.slice(1)" placeholder="Seleccione..." />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Sucursal <span class="text-destructive">*</span></label>
              <Select v-model="newForm.sucursalId" :options="sucOptions.slice(1)" placeholder="Seleccione..." />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Fecha y Hora <span class="text-destructive">*</span></label>
              <Input v-model="newForm.fechaHora" type="datetime-local" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Estudio</label>
              <Select v-model="newForm.estudioId" :options="estudioOptions" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Obra Social</label>
              <Select v-model="newForm.obraSocialId" :options="osOptions" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Plan</label>
              <Select v-model="newForm.planId" :options="planesForOs(newForm.obraSocialId)" placeholder="Plan..." />
            </div>
            <div class="space-y-1 sm:col-span-2">
              <label class="text-sm font-medium">Observaciones</label>
              <Input v-model="newForm.observaciones" placeholder="Notas del turno..." />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Asignar Turno" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Turnos Table -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando turnos...</div>
        <div v-else-if="filteredTurnos.length === 0" class="p-6 text-center text-muted-foreground">
          <CalendarDays class="h-8 w-8 mx-auto mb-2 opacity-50" />
          <p class="text-sm">No hay turnos para esta fecha</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="border-b bg-muted/50">
              <tr>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Hora</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Paciente</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Profesional</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Sucursal</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Estudio</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">O.S.</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Estado</th>
                <th class="px-4 py-3 text-right font-medium text-muted-foreground">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="t in filteredTurnos" :key="t.id">
                <tr class="border-b hover:bg-muted/30 transition-colors">
                  <td class="px-4 py-3">
                    <div class="flex items-center gap-1.5">
                      <Clock class="h-3.5 w-3.5 text-muted-foreground" />
                      <span class="font-mono font-medium">{{ formatTime(t.fechaHora) }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <div class="font-medium">{{ t.pacienteApellido }}, {{ t.pacienteNombre }}</div>
                    <div class="text-xs text-muted-foreground flex items-center gap-2">
                      <span>DNI {{ t.pacienteDni }}</span>
                      <span v-if="t.pacienteEmail" class="text-primary/70 truncate max-w-[140px]">{{ t.pacienteEmail }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3 text-muted-foreground">{{ t.profesionalApellido }}, {{ t.profesionalNombre }}</td>
                  <td class="px-4 py-3 text-muted-foreground text-xs">{{ t.sucursalNombre }}</td>
                  <td class="px-4 py-3 text-xs">{{ t.estudioNombre ?? "—" }}</td>
                  <td class="px-4 py-3">
                    <span v-if="t.obraSocialNombre" class="text-xs px-1.5 py-0.5 rounded bg-primary/10 text-primary">{{ t.obraSocialNombre }}</span>
                    <span v-else class="text-xs text-muted-foreground">—</span>
                  </td>
                  <td class="px-4 py-3">
                    <span :class="['text-xs px-2 py-0.5 rounded-full font-medium', estadoBadge[t.estado] ?? 'bg-muted']">
                      {{ t.estado.replace('_', ' ') }}
                    </span>
                  </td>
                  <td class="px-4 py-3 text-right">
                    <!-- Reprogramar inline -->
                    <div v-if="reprogramId === t.id" class="flex items-center justify-end gap-2">
                      <Input v-model="reprogramFecha" type="datetime-local" class="h-7 text-xs w-48" />
                      <button @click="doReprogramar" class="text-xs px-2 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90">OK</button>
                      <button @click="reprogramId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">X</button>
                    </div>
                    <div v-else class="flex items-center justify-end gap-1 flex-wrap">
                      <!-- Botón enviar confirmación por email -->
                      <button
                        v-if="t.estado === 'PENDIENTE' || t.estado === 'CONFIRMADO'"
                        @click="enviarConfirmacion(t)"
                        :disabled="enviandoEmailId === t.id"
                        :title="t.confirmacionEnviadaAt ? 'Reenviar confirmación · último envío ' + t.confirmacionEnviadaAt.substring(0,16).replace('T',' ') : 'Enviar confirmación por email'"
                        :class="['text-xs px-2 py-1 rounded transition-colors flex items-center gap-1',
                          t.confirmacionEnviadaAt
                            ? 'bg-green-100 text-green-700 hover:bg-green-200'
                            : 'bg-gray-100 text-gray-700 hover:bg-gray-200']">
                        <component :is="t.confirmacionEnviadaAt ? CheckCircle2 : Mail" class="h-3 w-3" />
                        <span v-if="enviandoEmailId === t.id">Enviando…</span>
                        <span v-else>{{ t.confirmacionEnviadaAt ? 'Reenviar' : 'Enviar' }}</span>
                      </button>
                      <button v-if="t.estado === 'PENDIENTE'" @click="cambiarEstado(t.id, 'CONFIRMADO')"
                        class="text-xs px-2 py-1 rounded bg-blue-100 text-blue-800 hover:bg-blue-200 transition-colors">Confirmar</button>
                      <button v-if="t.estado === 'CONFIRMADO'" @click="cambiarEstado(t.id, 'EN_ESPERA')"
                        class="text-xs px-2 py-1 rounded bg-orange-100 text-orange-800 hover:bg-orange-200 transition-colors">En Espera</button>
                      <button v-if="t.estado === 'EN_ESPERA'" @click="cambiarEstado(t.id, 'EN_CURSO')"
                        class="text-xs px-2 py-1 rounded bg-purple-100 text-purple-800 hover:bg-purple-200 transition-colors">En Curso</button>
                      <button v-if="t.estado === 'EN_CURSO'" @click="cambiarEstado(t.id, 'ATENDIDO')"
                        class="text-xs px-2 py-1 rounded bg-green-100 text-green-800 hover:bg-green-200 transition-colors">Atendido</button>
                      <button v-if="['PENDIENTE','CONFIRMADO','EN_ESPERA'].includes(t.estado)" @click="cambiarEstado(t.id, 'AUSENTE')"
                        class="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600 hover:bg-gray-200 transition-colors">Ausente</button>
                      <button v-if="t.estado !== 'CANCELADO' && t.estado !== 'COMPLETADO'" @click="startReprogramar(t)"
                        class="text-xs px-2 py-1 rounded border hover:bg-muted transition-colors" title="Reprogramar">
                        <RefreshCw class="h-3 w-3" />
                      </button>
                      <button v-if="t.estado !== 'CANCELADO' && t.estado !== 'COMPLETADO'" @click="cancelarTurno(t.id)"
                        class="text-xs px-2 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Cancelar</button>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>

    <!-- ══ Modal alta rápida de paciente ══ -->
    <Teleport to="body">
      <div v-if="showQuickPaciente"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="showQuickPaciente = false">
        <div class="bg-background rounded-xl shadow-2xl w-full max-w-lg">

          <!-- Header -->
          <div class="flex items-center justify-between px-6 py-4 border-b">
            <div>
              <h2 class="font-semibold text-base">Nuevo Paciente</h2>
              <p class="text-xs text-muted-foreground mt-0.5">
                Se creará y se seleccionará automáticamente en el turno
              </p>
            </div>
            <button @click="showQuickPaciente = false"
              class="p-1.5 rounded hover:bg-muted transition-colors text-muted-foreground">
              <X class="h-4 w-4" />
            </button>
          </div>

          <!-- Form -->
          <form @submit.prevent="saveQuickPaciente" class="px-6 py-5 space-y-4">
            <div v-if="quickError"
              class="rounded-md bg-destructive/10 border border-destructive/30 px-3 py-2 text-sm text-destructive">
              {{ quickError }}
            </div>

            <!-- Nombre + Apellido -->
            <div class="grid grid-cols-2 gap-3">
              <div class="space-y-1">
                <label class="text-sm font-medium">Nombre <span class="text-destructive">*</span></label>
                <Input v-model="quickForm.nombre" placeholder="Ej: María" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Apellido <span class="text-destructive">*</span></label>
                <Input v-model="quickForm.apellido" placeholder="Ej: González" />
              </div>
            </div>

            <!-- DNI + Fecha nac + Sexo -->
            <div class="grid grid-cols-3 gap-3">
              <div class="space-y-1">
                <label class="text-sm font-medium">DNI <span class="text-destructive">*</span></label>
                <Input v-model="quickForm.dni" placeholder="30100001" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Fecha Nac.</label>
                <Input v-model="quickForm.fechaNacimiento" type="date" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Sexo</label>
                <select v-model="quickForm.sexo"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option value="">-</option>
                  <option value="F">Femenino</option>
                  <option value="M">Masculino</option>
                  <option value="X">Otro</option>
                </select>
              </div>
            </div>

            <!-- Teléfono + Email -->
            <div class="grid grid-cols-2 gap-3">
              <div class="space-y-1">
                <label class="text-sm font-medium">Teléfono</label>
                <Input v-model="quickForm.telefono" placeholder="11-1234-5678" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Email</label>
                <Input v-model="quickForm.email" type="email" placeholder="mail@ejemplo.com" />
              </div>
            </div>

            <!-- Obra Social + Plan -->
            <div class="grid grid-cols-2 gap-3">
              <div class="space-y-1">
                <label class="text-sm font-medium">Obra Social</label>
                <select v-model="quickForm.obraSocialId"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option value="">Sin obra social</option>
                  <option v-for="o in obrasSociales" :key="o.id" :value="String(o.id)">{{ o.nombre }}</option>
                </select>
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Plan</label>
                <select v-model="quickForm.planId"
                  :disabled="!quickForm.obraSocialId"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring disabled:opacity-50">
                  <option value="">Sin plan</option>
                  <option v-for="p in planesForOs(quickForm.obraSocialId)" :key="p.value" :value="p.value">
                    {{ p.label }}
                  </option>
                </select>
              </div>
            </div>

            <!-- Acciones -->
            <div class="flex justify-end gap-2 pt-1">
              <button type="button" @click="showQuickPaciente = false"
                class="px-4 py-2 text-sm rounded-md border hover:bg-muted transition-colors">
                Cancelar
              </button>
              <button type="submit" :disabled="quickSaving"
                class="px-4 py-2 text-sm rounded-md bg-primary text-primary-foreground hover:bg-primary/90 transition-colors disabled:opacity-50 flex items-center gap-2">
                <span v-if="quickSaving" class="w-3 h-3 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                {{ quickSaving ? "Guardando..." : "Crear y seleccionar" }}
              </button>
            </div>
          </form>

        </div>
      </div>
    </Teleport>

    <!-- Toast de confirmación de email -->
    <Teleport to="body">
      <Transition name="toast">
        <div v-if="emailToast"
          :class="['fixed bottom-6 right-6 z-50 flex items-center gap-3 px-4 py-3 rounded-lg shadow-lg text-sm font-medium border',
            emailToast.ok
              ? 'bg-green-50 text-green-800 border-green-200'
              : 'bg-red-50 text-red-800 border-red-200']">
          <component :is="emailToast.ok ? CheckCircle2 : X"
            :class="['h-4 w-4 shrink-0', emailToast.ok ? 'text-green-600' : 'text-red-500']" />
          {{ emailToast.msg }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.toast-enter-active, .toast-leave-active { transition: all .3s ease; }
.toast-enter-from, .toast-leave-to { opacity: 0; transform: translateY(16px); }
</style>
