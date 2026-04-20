<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import {
  ChevronLeft, ChevronRight, CalendarDays, Calendar,
  RefreshCw, Filter, Plus, X, Trash2,
} from "lucide-vue-next";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";

const API_TURNOS  = "http://localhost:8080/api/turnos";
const API_PROF    = "http://localhost:8080/api/profesionales";
const API_AGENDA  = "http://localhost:8080/api/agenda";
const API_SUC     = "http://localhost:8080/api/sucursales";

const auth    = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

// ─── Roles ────────────────────────────────────────────────────────────────────
const isDoctor    = computed(() => auth.hasAnyRole(["ROLE_DOCTOR"]));
const isRecepcion = computed(() => auth.hasAnyRole(["ROLE_RECEPCION"]));
const isAdmin     = computed(() => auth.hasAnyRole(["ROLE_ADMIN"]));
const canFilter   = computed(() => isAdmin.value || isRecepcion.value);
const canManage   = computed(() => isAdmin.value);

// ─── Tab: "calendar" | "schedule" ────────────────────────────────────────────
type TabMode = "calendar" | "schedule";
const tab = ref<TabMode>("calendar");

// ═══════════════════════════════════════════════════════════════════════════════
//  CALENDAR TAB
// ═══════════════════════════════════════════════════════════════════════════════

type ViewMode = "week" | "month";
const viewMode = ref<ViewMode>("week");

// Anchor date
const anchor = ref<Date>(startOfWeek(new Date()));

function startOfWeek(d: Date): Date {
  const r = new Date(d);
  const day = r.getDay();
  const diff = day === 0 ? -6 : 1 - day; // Monday first
  r.setDate(r.getDate() + diff);
  r.setHours(0, 0, 0, 0);
  return r;
}
function startOfMonth(d: Date): Date {
  return new Date(d.getFullYear(), d.getMonth(), 1, 0, 0, 0, 0);
}

const rangeStart = computed<Date>(() =>
  viewMode.value === "week" ? anchor.value : startOfMonth(anchor.value),
);
const rangeEnd = computed<Date>(() => {
  if (viewMode.value === "week") {
    const d = new Date(anchor.value);
    d.setDate(d.getDate() + 7);
    return d;
  }
  return new Date(anchor.value.getFullYear(), anchor.value.getMonth() + 1, 1);
});

const days = computed<Date[]>(() => {
  const result: Date[] = [];
  const cur = new Date(rangeStart.value);
  while (cur < rangeEnd.value) {
    result.push(new Date(cur));
    cur.setDate(cur.getDate() + 1);
  }
  return result;
});

const rangeLabel = computed<string>(() => {
  if (viewMode.value === "month") {
    return new Intl.DateTimeFormat("es-AR", { month: "long", year: "numeric" })
      .format(rangeStart.value)
      .replace(/^./, c => c.toUpperCase());
  }
  const s = rangeStart.value;
  const e = new Date(rangeEnd.value); e.setDate(e.getDate() - 1);
  const fmtM = new Intl.DateTimeFormat("es-AR", { month: "short" });
  if (s.getFullYear() === e.getFullYear() && s.getMonth() === e.getMonth()) {
    return `${s.getDate()} – ${e.getDate()} ${new Intl.DateTimeFormat("es-AR", { month: "long" }).format(s)} ${s.getFullYear()}`;
  }
  return `${s.getDate()} ${fmtM.format(s)} – ${e.getDate()} ${fmtM.format(e)} ${e.getFullYear()}`;
});

function navigate(direction: 1 | -1) {
  const d = new Date(anchor.value);
  if (viewMode.value === "week") {
    d.setDate(d.getDate() + direction * 7);
    anchor.value = startOfWeek(d);
  } else {
    d.setMonth(d.getMonth() + direction);
    anchor.value = startOfMonth(d);
  }
}
function goToday() {
  anchor.value = viewMode.value === "week" ? startOfWeek(new Date()) : startOfMonth(new Date());
}

watch(viewMode, () => {
  anchor.value = viewMode.value === "week"
    ? startOfWeek(anchor.value)
    : startOfMonth(anchor.value);
});

// ─── Professionals for filter ─────────────────────────────────────────────────
interface ProfDto { id: number; nombre: string; apellido: string; }
const profesionales  = ref<ProfDto[]>([]);
const selectedProfId = ref<number | "">("");
const myProfId       = ref<number | null>(null);

async function loadProfesionales() {
  try {
    const res = await axios.get(API_PROF, { headers: headers() });
    profesionales.value = res.data;
  } catch { /* silent */ }
}

async function loadMyProf() {
  if (!isDoctor.value) return;
  try {
    const res = await axios.get(`${API_PROF}/me`, { headers: headers() });
    myProfId.value = res.data?.id ?? null;
  } catch { /* silent */ }
}

// ─── Turnos ───────────────────────────────────────────────────────────────────
interface TurnoDto {
  id: number;
  pacienteId: number;
  pacienteNombre: string;
  pacienteApellido: string;
  profesionalId: number;
  profesionalNombre: string;
  profesionalApellido: string;
  sucursalId: number;
  sucursalNombre: string;
  estudioNombre: string | null;
  obraSocialNombre: string | null;
  fechaHora: string;
  estado: string;
  observaciones: string | null;
}

const turnos  = ref<TurnoDto[]>([]);
const loading = ref(false);
const error   = ref("");

function toIso(d: Date): string {
  // Usa hora LOCAL, no UTC (evita desfase en Argentina UTC-3)
  const p = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

async function loadTurnos() {
  loading.value = true;
  error.value   = "";
  try {
    const params: Record<string, string> = {
      desde: toIso(rangeStart.value),
      hasta: toIso(rangeEnd.value),
    };
    const profId = isDoctor.value ? myProfId.value : (selectedProfId.value || null);
    if (profId) params.profesionalId = String(profId);
    const res = await axios.get(API_TURNOS, { headers: headers(), params });
    turnos.value = res.data;
  } catch {
    error.value = "Error al cargar los turnos.";
  } finally {
    loading.value = false;
  }
}

watch([rangeStart, rangeEnd, selectedProfId, myProfId], loadTurnos);

function turnosForDay(day: Date): TurnoDto[] {
  const ds = day.toDateString();
  return turnos.value
    .filter(t => new Date(t.fechaHora).toDateString() === ds)
    .sort((a, b) => a.fechaHora.localeCompare(b.fechaHora));
}

function formatTime(iso: string): string {
  return new Date(iso).toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" });
}

const today = new Date();
const isToday = (d: Date) => d.toDateString() === today.toDateString();
const isPast  = (d: Date) => d < today && !isToday(d);

const estadoClass: Record<string, string> = {
  PENDIENTE:  "bg-yellow-100 text-yellow-800 border-yellow-200",
  CONFIRMADO: "bg-blue-100 text-blue-800 border-blue-200",
  ATENDIDO:   "bg-green-100 text-green-800 border-green-200",
  CANCELADO:  "bg-red-100 text-red-800 border-red-200",
  AUSENTE:    "bg-gray-100 text-gray-600 border-gray-200",
};
const estadoDot: Record<string, string> = {
  PENDIENTE:  "bg-yellow-400",
  CONFIRMADO: "bg-blue-500",
  ATENDIDO:   "bg-green-500",
  CANCELADO:  "bg-red-400",
  AUSENTE:    "bg-gray-400",
};

// Month grid (padding cells for Mon-first layout)
const monthGrid = computed<(Date | null)[]>(() => {
  if (viewMode.value !== "month") return [];
  const first = rangeStart.value;
  const firstDow = first.getDay(); // 0=Sun
  const padBefore = firstDow === 0 ? 6 : firstDow - 1;
  const grid: (Date | null)[] = Array(padBefore).fill(null);
  days.value.forEach(d => grid.push(d));
  while (grid.length % 7 !== 0) grid.push(null);
  return grid;
});

// Turno detail modal
const selectedTurno = ref<TurnoDto | null>(null);

// ═══════════════════════════════════════════════════════════════════════════════
//  SCHEDULE TAB (Agenda config)
// ═══════════════════════════════════════════════════════════════════════════════

interface AgendaDto {
  id: number;
  profesionalId: number;
  profesionalNombre: string;
  sucursalId: number;
  sucursalNombre: string;
  diaSemana: number;
  horaInicio: string;
  horaFin: string;
  duracionTurnoMinutos: number;
  activa: boolean;
}

const agendas      = ref<AgendaDto[]>([]);
const sucursales   = ref<{ id: number; nombre: string }[]>([]);
const schedLoading = ref(false);
const schedError   = ref("");
const filterProfId = ref("");
const filterSucId  = ref("");

const showNewForm = ref(false);
const newForm     = ref({ profesionalId: "", sucursalId: "", diaSemana: "", horaInicio: "08:00", horaFin: "12:00", duracionTurnoMinutos: "30" });
const newSaving   = ref(false);
const newError    = ref("");

const diasSemana = [
  { value: "1", label: "Lunes" }, { value: "2", label: "Martes" },
  { value: "3", label: "Miércoles" }, { value: "4", label: "Jueves" },
  { value: "5", label: "Viernes" }, { value: "6", label: "Sábado" },
];
const diaLabel: Record<number, string> = { 1: "Lunes", 2: "Martes", 3: "Miércoles", 4: "Jueves", 5: "Viernes", 6: "Sábado", 7: "Domingo" };
const duraciones = ["15", "20", "30", "40", "45", "60"];

const filteredAgendas = computed(() => {
  let list = agendas.value;
  // Doctor solo ve su propia agenda
  if (isDoctor.value && myProfId.value) {
    list = list.filter(a => a.profesionalId === myProfId.value);
  } else {
    if (filterProfId.value) list = list.filter(a => a.profesionalId === Number(filterProfId.value));
  }
  if (filterSucId.value)  list = list.filter(a => a.sucursalId   === Number(filterSucId.value));
  return list.sort((a, b) => a.diaSemana - b.diaSemana || a.horaInicio.localeCompare(b.horaInicio));
});

const groupedAgendas = computed(() => {
  const groups: Record<string, AgendaDto[]> = {};
  for (const a of filteredAgendas.value) {
    if (!groups[a.profesionalNombre]) groups[a.profesionalNombre] = [];
    groups[a.profesionalNombre].push(a);
  }
  return groups;
});

async function loadSchedule() {
  schedLoading.value = true;
  schedError.value   = "";
  try {
    const [agRes, sucRes] = await Promise.all([
      axios.get(API_AGENDA,  { headers: headers() }),
      axios.get(API_SUC,     { headers: headers() }),
    ]);
    agendas.value    = agRes.data;
    sucursales.value = sucRes.data;
  } catch { schedError.value = "Error al cargar agendas."; }
  finally { schedLoading.value = false; }
}

async function createAgenda() {
  newError.value = "";
  if (!newForm.value.profesionalId || !newForm.value.sucursalId || !newForm.value.diaSemana) {
    newError.value = "Profesional, sucursal y día son obligatorios."; return;
  }
  newSaving.value = true;
  try {
    await axios.post(API_AGENDA, {
      profesionalId: Number(newForm.value.profesionalId),
      sucursalId:    Number(newForm.value.sucursalId),
      diaSemana:     Number(newForm.value.diaSemana),
      horaInicio:    newForm.value.horaInicio,
      horaFin:       newForm.value.horaFin,
      duracionTurnoMinutos: Number(newForm.value.duracionTurnoMinutos),
    }, { headers: headers() });
    showNewForm.value = false;
    newForm.value = { profesionalId: "", sucursalId: "", diaSemana: "", horaInicio: "08:00", horaFin: "12:00", duracionTurnoMinutos: "30" };
    await loadSchedule();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear.";
  } finally { newSaving.value = false; }
}

async function deleteAgenda(id: number) {
  try {
    await axios.delete(`${API_AGENDA}/${id}`, { headers: headers() });
    await loadSchedule();
  } catch (e: any) {
    schedError.value = e?.response?.data?.error ?? "Error al eliminar.";
    setTimeout(() => { schedError.value = ""; }, 3000);
  }
}

// ─── Bootstrap ────────────────────────────────────────────────────────────────
onMounted(async () => {
  await Promise.all([loadProfesionales(), loadMyProf()]);
  await Promise.all([loadTurnos(), loadSchedule()]);
});
</script>

<template>
  <div class="space-y-4">
    <!-- Page header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
      <div>
        <h1 class="text-2xl font-bold">{{ isDoctor ? "Mi Agenda" : "Agenda Médica" }}</h1>
        <p class="text-muted-foreground text-sm">
          {{ isDoctor ? "Tus turnos programados" : "Turnos y horarios por profesional y sucursal" }}
        </p>
      </div>
    </div>

    <!-- Tabs -->
    <div class="flex border-b gap-1">
      <button
        @click="tab = 'calendar'"
        :class="['px-4 py-2 text-sm font-medium transition-colors -mb-px',
          tab === 'calendar'
            ? 'border-b-2 border-primary text-primary'
            : 'text-muted-foreground hover:text-foreground']">
        <span class="flex items-center gap-1.5"><CalendarDays class="h-4 w-4" /> Calendario de Turnos</span>
      </button>
      <button
        @click="tab = 'schedule'"
        :class="['px-4 py-2 text-sm font-medium transition-colors -mb-px',
          tab === 'schedule'
            ? 'border-b-2 border-primary text-primary'
            : 'text-muted-foreground hover:text-foreground']">
        <span class="flex items-center gap-1.5"><Calendar class="h-4 w-4" /> Horarios Configurados</span>
      </button>
    </div>

    <!-- ═══════════ CALENDAR TAB ═══════════ -->
    <template v-if="tab === 'calendar'">

      <!-- Controls row -->
      <div class="flex flex-wrap items-center gap-2">
        <!-- Navigation -->
        <button @click="navigate(-1)" class="p-1.5 rounded border hover:bg-muted transition-colors">
          <ChevronLeft class="h-4 w-4" />
        </button>
        <button @click="navigate(1)" class="p-1.5 rounded border hover:bg-muted transition-colors">
          <ChevronRight class="h-4 w-4" />
        </button>
        <span class="font-semibold text-sm sm:text-base min-w-[200px]">{{ rangeLabel }}</span>
        <button @click="goToday"
          class="text-xs px-3 py-1.5 rounded border hover:bg-muted transition-colors">
          Hoy
        </button>

        <div class="flex-1" />

        <!-- View toggle -->
        <div class="flex rounded-md border overflow-hidden text-sm">
          <button @click="viewMode = 'week'"
            :class="['px-3 py-1.5 flex items-center gap-1.5 transition-colors',
              viewMode === 'week' ? 'bg-primary text-primary-foreground' : 'hover:bg-muted']">
            <CalendarDays class="h-3.5 w-3.5" /> Semana
          </button>
          <button @click="viewMode = 'month'"
            :class="['px-3 py-1.5 flex items-center gap-1.5 border-l transition-colors',
              viewMode === 'month' ? 'bg-primary text-primary-foreground' : 'hover:bg-muted']">
            <Calendar class="h-3.5 w-3.5" /> Mes
          </button>
        </div>
        <button @click="loadTurnos" :disabled="loading"
          class="p-1.5 rounded border hover:bg-muted transition-colors" title="Actualizar">
          <RefreshCw :class="['h-4 w-4', loading && 'animate-spin']" />
        </button>
      </div>

      <!-- Professional filter -->
      <div v-if="canFilter" class="flex items-center gap-2">
        <Filter class="h-4 w-4 text-muted-foreground shrink-0" />
        <select v-model="selectedProfId"
          class="text-sm border rounded-md px-3 py-1.5 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
          <option value="">Todos los profesionales</option>
          <option v-for="p in profesionales" :key="p.id" :value="p.id">
            {{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}
          </option>
        </select>
      </div>

      <!-- Error -->
      <div v-if="error" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
        {{ error }}
      </div>

      <!-- ─── WEEK VIEW ─── -->
      <div v-if="viewMode === 'week'" class="grid grid-cols-7 gap-1">
        <!-- Day headers -->
        <div v-for="day in days" :key="'h-' + day.toISOString()"
          :class="['text-center pb-1 border-b', isToday(day) ? 'border-primary' : 'border-border']">
          <div class="text-xs text-muted-foreground font-medium uppercase">
            {{ ["Dom","Lun","Mar","Mié","Jue","Vie","Sáb"][day.getDay()] }}
          </div>
          <div :class="['text-sm font-semibold mx-auto w-7 h-7 flex items-center justify-center rounded-full mt-0.5',
            isToday(day) ? 'bg-primary text-primary-foreground' : '']">
            {{ day.getDate() }}
          </div>
        </div>

        <!-- Day cells -->
        <div v-for="day in days" :key="'c-' + day.toISOString()"
          :class="['min-h-[380px] rounded-md p-1 space-y-1 border transition-colors',
            isToday(day) ? 'bg-primary/5 border-primary/25' : 'bg-muted/20 border-transparent',
            isPast(day) ? 'opacity-60' : '']">
          <div v-if="loading" class="flex justify-center pt-4">
            <div class="w-4 h-4 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
          </div>
          <template v-else>
            <button
              v-for="t in turnosForDay(day)" :key="t.id"
              @click="selectedTurno = t"
              :class="['w-full text-left rounded border px-1.5 py-1 text-xs hover:opacity-80 transition-opacity',
                estadoClass[t.estado] ?? 'bg-gray-100 text-gray-700 border-gray-200']">
              <div class="font-semibold">{{ formatTime(t.fechaHora) }}</div>
              <div class="truncate">{{ t.pacienteApellido }}, {{ t.pacienteNombre }}</div>
              <div v-if="!isDoctor" class="truncate text-[10px] opacity-70">
                {{ t.profesionalApellido }} {{ t.profesionalNombre[0] }}.
              </div>
            </button>
            <div v-if="turnosForDay(day).length === 0"
              class="text-[10px] text-muted-foreground text-center pt-3 opacity-60">Sin turnos</div>
          </template>
        </div>
      </div>

      <!-- ─── MONTH VIEW ─── -->
      <div v-else>
        <div class="grid grid-cols-7 mb-1">
          <div v-for="d in ['Lun','Mar','Mié','Jue','Vie','Sáb','Dom']" :key="d"
            class="text-center text-xs font-medium text-muted-foreground py-1 uppercase">{{ d }}</div>
        </div>
        <div class="grid grid-cols-7 gap-1">
          <template v-for="(cell, idx) in monthGrid" :key="idx">
            <div v-if="!cell" class="min-h-[90px] rounded-md bg-muted/10 border border-dashed border-border/30"></div>
            <div v-else
              :class="['min-h-[90px] rounded-md p-1 border transition-colors',
                isToday(cell) ? 'bg-primary/5 border-primary/30' : 'bg-background border-border',
                isPast(cell) ? 'opacity-60' : '']">
              <div :class="['text-xs font-semibold mb-1 w-5 h-5 flex items-center justify-center rounded-full',
                isToday(cell) ? 'bg-primary text-primary-foreground' : 'text-muted-foreground']">
                {{ cell.getDate() }}
              </div>
              <div v-if="loading" class="flex justify-center py-1">
                <div class="w-3 h-3 border-2 border-primary border-t-transparent rounded-full animate-spin"></div>
              </div>
              <template v-else>
                <button
                  v-for="t in turnosForDay(cell).slice(0, 3)" :key="t.id"
                  @click="selectedTurno = t"
                  :class="['w-full flex items-center gap-1 px-1 py-0.5 rounded text-[10px] hover:opacity-80 transition-opacity mb-0.5 border',
                    estadoClass[t.estado] ?? 'bg-gray-100 text-gray-700 border-gray-200']">
                  <span :class="['inline-block w-1.5 h-1.5 rounded-full shrink-0', estadoDot[t.estado] ?? 'bg-gray-400']"></span>
                  <span class="truncate">{{ formatTime(t.fechaHora) }} {{ t.pacienteApellido }}</span>
                </button>
                <div v-if="turnosForDay(cell).length > 3"
                  class="text-[10px] text-primary font-medium px-1">
                  +{{ turnosForDay(cell).length - 3 }} más
                </div>
              </template>
            </div>
          </template>
        </div>
      </div>

      <!-- Legend -->
      <div class="flex flex-wrap gap-3 pt-1">
        <div v-for="(cls, label) in estadoClass" :key="label"
          :class="['flex items-center gap-1.5 text-xs px-2 py-0.5 rounded border', cls]">
          <span :class="['w-1.5 h-1.5 rounded-full', estadoDot[label]]"></span>
          {{ label.charAt(0) + label.slice(1).toLowerCase() }}
        </div>
      </div>
    </template>

    <!-- ═══════════ SCHEDULE TAB ═══════════ -->
    <template v-else>
      <div class="flex items-center justify-between">
        <p class="text-sm text-muted-foreground">Horarios de atención configurados por profesional</p>
        <button v-if="canManage" @click="showNewForm = !showNewForm"
          class="inline-flex items-center gap-2 rounded-md bg-primary px-3 py-1.5 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors">
          <component :is="showNewForm ? X : Plus" class="h-3.5 w-3.5" />
          {{ showNewForm ? "Cancelar" : "Nuevo Horario" }}
        </button>
      </div>

      <!-- Schedule error -->
      <div v-if="schedError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
        {{ schedError }}
      </div>

      <!-- Filters (solo admin/recepcion) -->
      <div v-if="!isDoctor" class="flex flex-wrap gap-2">
        <select v-model="filterProfId"
          class="text-sm border rounded-md px-3 py-1.5 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
          <option value="">Todos los profesionales</option>
          <option v-for="p in profesionales" :key="p.id" :value="String(p.id)">
            {{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}
          </option>
        </select>
        <select v-model="filterSucId"
          class="text-sm border rounded-md px-3 py-1.5 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
          <option value="">Todas las sucursales</option>
          <option v-for="s in sucursales" :key="s.id" :value="String(s.id)">{{ s.nombre }}</option>
        </select>
      </div>
      <!-- Doctor: solo filtra por sucursal -->
      <div v-else class="flex flex-wrap gap-2">
        <select v-model="filterSucId"
          class="text-sm border rounded-md px-3 py-1.5 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
          <option value="">Todas las sucursales</option>
          <option v-for="s in sucursales" :key="s.id" :value="String(s.id)">{{ s.nombre }}</option>
        </select>
      </div>

      <!-- New Agenda Form -->
      <Card v-if="showNewForm && canManage">
        <CardHeader><CardTitle class="text-base">Agregar horario de atención</CardTitle></CardHeader>
        <CardContent>
          <form @submit.prevent="createAgenda" class="space-y-4">
            <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <div class="space-y-1">
                <label class="text-sm font-medium">Profesional <span class="text-destructive">*</span></label>
                <select v-model="newForm.profesionalId"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option value="">Seleccione...</option>
                  <option v-for="p in profesionales" :key="p.id" :value="String(p.id)">
                    {{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}
                  </option>
                </select>
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Sucursal <span class="text-destructive">*</span></label>
                <select v-model="newForm.sucursalId"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option value="">Seleccione...</option>
                  <option v-for="s in sucursales" :key="s.id" :value="String(s.id)">{{ s.nombre }}</option>
                </select>
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Día <span class="text-destructive">*</span></label>
                <select v-model="newForm.diaSemana"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option value="">Seleccione día...</option>
                  <option v-for="d in diasSemana" :key="d.value" :value="d.value">{{ d.label }}</option>
                </select>
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Hora Inicio</label>
                <Input v-model="newForm.horaInicio" type="time" class="text-sm" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Hora Fin</label>
                <Input v-model="newForm.horaFin" type="time" class="text-sm" />
              </div>
              <div class="space-y-1">
                <label class="text-sm font-medium">Duración Turno</label>
                <select v-model="newForm.duracionTurnoMinutos"
                  class="w-full text-sm border rounded-md px-3 py-2 bg-background focus:outline-none focus:ring-2 focus:ring-ring">
                  <option v-for="d in duraciones" :key="d" :value="d">{{ d }} min</option>
                </select>
              </div>
            </div>
            <div class="flex justify-end gap-2">
              <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
              <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Agregar Horario" }}</Button>
            </div>
          </form>
        </CardContent>
      </Card>

      <!-- Agenda list -->
      <div v-if="schedLoading" class="p-6 text-sm text-muted-foreground text-center">Cargando...</div>
      <div v-else-if="Object.keys(groupedAgendas).length === 0"
        class="p-8 text-center text-muted-foreground">
        <Calendar class="h-8 w-8 mx-auto mb-2 opacity-40" />
        <p class="text-sm">No hay horarios configurados</p>
      </div>
      <div v-else class="space-y-4">
        <Card v-for="(items, profName) in groupedAgendas" :key="profName">
          <CardHeader class="pb-2">
            <CardTitle class="text-base flex items-center gap-2">
              <span class="w-7 h-7 rounded-full bg-primary/10 flex items-center justify-center text-xs font-bold text-primary">
                {{ String(profName)[0] }}
              </span>
              {{ profName }}
            </CardTitle>
          </CardHeader>
          <CardContent class="p-0">
            <table class="w-full text-sm">
              <thead class="border-b bg-muted/50">
                <tr>
                  <th class="px-4 py-2 text-left font-medium text-muted-foreground">Día</th>
                  <th class="px-4 py-2 text-left font-medium text-muted-foreground">Horario</th>
                  <th class="px-4 py-2 text-left font-medium text-muted-foreground">Duración</th>
                  <th class="px-4 py-2 text-left font-medium text-muted-foreground">Sucursal</th>
                  <th v-if="canManage" class="px-4 py-2 text-right"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="a in items" :key="a.id" class="border-b hover:bg-muted/30 transition-colors">
                  <td class="px-4 py-2">
                    <span class="text-xs px-2 py-0.5 rounded-full bg-primary/10 text-primary font-medium">
                      {{ diaLabel[a.diaSemana] }}
                    </span>
                  </td>
                  <td class="px-4 py-2 font-mono text-xs">{{ a.horaInicio }} – {{ a.horaFin }}</td>
                  <td class="px-4 py-2 text-muted-foreground">{{ a.duracionTurnoMinutos }} min</td>
                  <td class="px-4 py-2">
                    <span class="text-xs px-1.5 py-0.5 rounded bg-muted">{{ a.sucursalNombre }}</span>
                  </td>
                  <td v-if="canManage" class="px-4 py-2 text-right">
                    <button @click="deleteAgenda(a.id)"
                      class="text-muted-foreground hover:text-destructive transition-colors" title="Eliminar">
                      <Trash2 class="h-4 w-4" />
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </CardContent>
        </Card>
      </div>
    </template>

    <!-- ═══════════ TURNO DETAIL MODAL ═══════════ -->
    <Teleport to="body">
      <div v-if="selectedTurno"
        class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50"
        @click.self="selectedTurno = null">
        <div class="bg-background rounded-lg shadow-xl w-full max-w-sm">
          <div class="flex items-center justify-between px-5 py-4 border-b">
            <h2 class="font-semibold">Detalle del Turno</h2>
            <button @click="selectedTurno = null" class="p-1 rounded hover:bg-muted text-lg leading-none">&times;</button>
          </div>
          <div class="px-5 py-4 space-y-3 text-sm">
            <span :class="['inline-flex items-center gap-1.5 px-2 py-0.5 rounded-full text-xs font-medium border',
              estadoClass[selectedTurno.estado] ?? 'bg-gray-100 text-gray-700 border-gray-200']">
              <span :class="['w-1.5 h-1.5 rounded-full', estadoDot[selectedTurno.estado]]"></span>
              {{ selectedTurno.estado }}
            </span>
            <div class="grid grid-cols-2 gap-y-2 gap-x-4">
              <div>
                <div class="text-xs text-muted-foreground">Fecha y Hora</div>
                <div class="font-medium">
                  {{ new Date(selectedTurno.fechaHora).toLocaleDateString("es-AR") }}
                  {{ formatTime(selectedTurno.fechaHora) }}
                </div>
              </div>
              <div>
                <div class="text-xs text-muted-foreground">Sucursal</div>
                <div class="font-medium">{{ selectedTurno.sucursalNombre }}</div>
              </div>
              <div class="col-span-2">
                <div class="text-xs text-muted-foreground">Paciente</div>
                <div class="font-medium">{{ selectedTurno.pacienteApellido }}, {{ selectedTurno.pacienteNombre }}</div>
              </div>
              <div class="col-span-2">
                <div class="text-xs text-muted-foreground">Profesional</div>
                <div class="font-medium">{{ selectedTurno.profesionalApellido }}, {{ selectedTurno.profesionalNombre }}</div>
              </div>
              <div v-if="selectedTurno.estudioNombre" class="col-span-2">
                <div class="text-xs text-muted-foreground">Estudio</div>
                <div>{{ selectedTurno.estudioNombre }}</div>
              </div>
              <div v-if="selectedTurno.obraSocialNombre" class="col-span-2">
                <div class="text-xs text-muted-foreground">Obra Social</div>
                <div>{{ selectedTurno.obraSocialNombre }}</div>
              </div>
              <div v-if="selectedTurno.observaciones" class="col-span-2">
                <div class="text-xs text-muted-foreground">Observaciones</div>
                <div class="text-muted-foreground">{{ selectedTurno.observaciones }}</div>
              </div>
            </div>
          </div>
          <div class="px-5 py-3 border-t flex justify-end">
            <button @click="selectedTurno = null"
              class="px-4 py-1.5 text-sm rounded border hover:bg-muted transition-colors">Cerrar</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>
