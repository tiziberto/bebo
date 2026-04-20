<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import { useAuthStore } from "@/stores/auth";
import {
  Calendar, Clock, CheckCircle2, XCircle,
  UserRound, Stethoscope, RefreshCw, ChevronRight,
} from "lucide-vue-next";
import { useRouter } from "vue-router";

const API_PROF   = "http://localhost:8080/api/profesionales";
const API_TURNOS = "http://localhost:8080/api/turnos";

const auth    = useAuthStore();
const router  = useRouter();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

// ─── Estado ──────────────────────────────────────────────────────────────────
interface TurnoDto {
  id: number;
  pacienteNombre: string;
  pacienteApellido: string;
  sucursalNombre: string;
  estudioNombre: string | null;
  obraSocialNombre: string | null;
  fechaHora: string;
  estado: string;
  observaciones: string | null;
}

const turnos   = ref<TurnoDto[]>([]);
const loading  = ref(true);
const error    = ref("");
const myProfId = ref<number | null>(null);

// ─── Fecha de hoy ─────────────────────────────────────────────────────────────
const todayStart = new Date();
todayStart.setHours(0, 0, 0, 0);
const todayEnd = new Date();
todayEnd.setHours(23, 59, 59, 999);

const ahora = ref(new Date());
// Actualizar "ahora" cada minuto para el turno en curso
setInterval(() => { ahora.value = new Date(); }, 60_000);

function toIso(d: Date) {
  const p = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth()+1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

// ─── Carga de datos ───────────────────────────────────────────────────────────
async function load() {
  loading.value = true;
  error.value   = "";
  try {
    // 1. Obtener mi ID de profesional
    const profRes = await axios.get(`${API_PROF}/me`, { headers: headers() });
    myProfId.value = profRes.data?.id ?? null;

    if (!myProfId.value) { error.value = "No se encontró tu perfil de profesional."; return; }

    // 2. Turnos de hoy
    const res = await axios.get(API_TURNOS, {
      headers: headers(),
      params: {
        desde: toIso(todayStart),
        hasta: toIso(todayEnd),
        profesionalId: myProfId.value,
      },
    });
    turnos.value = (res.data as TurnoDto[]).sort(
      (a, b) => a.fechaHora.localeCompare(b.fechaHora),
    );
  } catch {
    error.value = "Error al cargar los turnos.";
  } finally {
    loading.value = false;
  }
}

onMounted(load);

// ─── Computed stats ───────────────────────────────────────────────────────────
const total      = computed(() => turnos.value.length);
const atendidos  = computed(() => turnos.value.filter(t => t.estado === "ATENDIDO").length);
const pendientes = computed(() => turnos.value.filter(t => ["PENDIENTE","CONFIRMADO"].includes(t.estado)).length);
const cancelados = computed(() => turnos.value.filter(t => ["CANCELADO","AUSENTE"].includes(t.estado)).length);

// Turno "en curso": el que tiene hora más próxima pasada o futura dentro de ±30 min
const turnoEnCurso = computed(() => {
  const now = ahora.value.getTime();
  return turnos.value.find(t => {
    if (!["PENDIENTE","CONFIRMADO"].includes(t.estado)) return false;
    const diff = new Date(t.fechaHora).getTime() - now;
    return diff >= -30 * 60_000 && diff <= 30 * 60_000;
  }) ?? null;
});

// Próximo turno pendiente
const proximoTurno = computed(() =>
  turnos.value.find(t =>
    ["PENDIENTE","CONFIRMADO"].includes(t.estado) &&
    new Date(t.fechaHora).getTime() > ahora.value.getTime()
  ) ?? null,
);

// ─── Helpers visuales ─────────────────────────────────────────────────────────
function formatHora(iso: string) {
  return new Date(iso).toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" });
}

const estadoCfg: Record<string, { label: string; dot: string; row: string }> = {
  PENDIENTE:  { label: "Pendiente",  dot: "bg-yellow-400", row: "" },
  CONFIRMADO: { label: "Confirmado", dot: "bg-blue-500",   row: "bg-blue-50/60" },
  ATENDIDO:   { label: "Atendido",   dot: "bg-green-500",  row: "bg-green-50/40 opacity-70" },
  CANCELADO:  { label: "Cancelado",  dot: "bg-red-400",    row: "opacity-50" },
  AUSENTE:    { label: "Ausente",    dot: "bg-gray-400",   row: "opacity-50" },
};

function isNext(t: TurnoDto) {
  return proximoTurno.value?.id === t.id;
}
function isCurrent(t: TurnoDto) {
  return turnoEnCurso.value?.id === t.id;
}

// Hora actual formateada
const horaActual = computed(() =>
  ahora.value.toLocaleTimeString("es-AR", { hour: "2-digit", minute: "2-digit" }),
);
const fechaHoy = computed(() =>
  new Date().toLocaleDateString("es-AR", { weekday: "long", day: "numeric", month: "long", year: "numeric" })
    .replace(/^./, c => c.toUpperCase()),
);
</script>

<template>
  <div class="space-y-5">

    <!-- Header -->
    <div class="flex items-start justify-between">
      <div>
        <h1 class="text-2xl font-bold">Bienvenido, {{ auth.username }}</h1>
        <p class="text-muted-foreground text-sm capitalize">{{ fechaHoy }}</p>
      </div>
      <div class="flex items-center gap-3">
        <span class="text-2xl font-mono font-semibold tabular-nums">{{ horaActual }}</span>
        <button @click="load" :disabled="loading"
          class="p-1.5 rounded border hover:bg-muted transition-colors" title="Actualizar">
          <RefreshCw :class="['h-4 w-4', loading && 'animate-spin']" />
        </button>
      </div>
    </div>

    <!-- Error -->
    <div v-if="error" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
      {{ error }}
    </div>

    <!-- Stats cards -->
    <div class="grid grid-cols-2 sm:grid-cols-4 gap-3">
      <Card>
        <CardContent class="p-4 flex items-center gap-3">
          <div class="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
            <Calendar class="h-4 w-4 text-primary" />
          </div>
          <div>
            <p class="text-xl font-bold">{{ loading ? "—" : total }}</p>
            <p class="text-xs text-muted-foreground">Turnos hoy</p>
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4 flex items-center gap-3">
          <div class="w-9 h-9 rounded-full bg-yellow-100 flex items-center justify-center shrink-0">
            <Clock class="h-4 w-4 text-yellow-600" />
          </div>
          <div>
            <p class="text-xl font-bold">{{ loading ? "—" : pendientes }}</p>
            <p class="text-xs text-muted-foreground">Pendientes</p>
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4 flex items-center gap-3">
          <div class="w-9 h-9 rounded-full bg-green-100 flex items-center justify-center shrink-0">
            <CheckCircle2 class="h-4 w-4 text-green-600" />
          </div>
          <div>
            <p class="text-xl font-bold">{{ loading ? "—" : atendidos }}</p>
            <p class="text-xs text-muted-foreground">Atendidos</p>
          </div>
        </CardContent>
      </Card>
      <Card>
        <CardContent class="p-4 flex items-center gap-3">
          <div class="w-9 h-9 rounded-full bg-red-100 flex items-center justify-center shrink-0">
            <XCircle class="h-4 w-4 text-red-500" />
          </div>
          <div>
            <p class="text-xl font-bold">{{ loading ? "—" : cancelados }}</p>
            <p class="text-xs text-muted-foreground">Cancelados/Ausentes</p>
          </div>
        </CardContent>
      </Card>
    </div>

    <!-- Turno en curso / próximo destacado -->
    <div v-if="!loading && (turnoEnCurso || proximoTurno)" class="grid sm:grid-cols-2 gap-3">
      <!-- En curso -->
      <div v-if="turnoEnCurso"
        class="rounded-lg border-2 border-blue-400 bg-blue-50 p-4 flex items-start gap-3">
        <div class="w-10 h-10 rounded-full bg-blue-500 flex items-center justify-center shrink-0 mt-0.5">
          <Stethoscope class="h-5 w-5 text-white" />
        </div>
        <div class="min-w-0">
          <div class="flex items-center gap-2 mb-0.5">
            <span class="inline-flex items-center gap-1 text-xs font-semibold text-blue-700 bg-blue-200 px-2 py-0.5 rounded-full">
              <span class="w-1.5 h-1.5 bg-blue-600 rounded-full animate-pulse"></span>
              En curso
            </span>
            <span class="text-xs text-blue-600 font-mono">{{ formatHora(turnoEnCurso.fechaHora) }}</span>
          </div>
          <p class="font-semibold truncate">{{ turnoEnCurso.pacienteApellido }}, {{ turnoEnCurso.pacienteNombre }}</p>
          <p class="text-sm text-blue-700 truncate">{{ turnoEnCurso.estudioNombre ?? "Sin estudio asignado" }}</p>
          <p class="text-xs text-blue-500 truncate">{{ turnoEnCurso.sucursalNombre }}
            <span v-if="turnoEnCurso.obraSocialNombre"> · {{ turnoEnCurso.obraSocialNombre }}</span>
          </p>
        </div>
      </div>

      <!-- Próximo -->
      <div v-if="proximoTurno && proximoTurno.id !== turnoEnCurso?.id"
        class="rounded-lg border border-yellow-300 bg-yellow-50 p-4 flex items-start gap-3">
        <div class="w-10 h-10 rounded-full bg-yellow-400 flex items-center justify-center shrink-0 mt-0.5">
          <ChevronRight class="h-5 w-5 text-white" />
        </div>
        <div class="min-w-0">
          <div class="flex items-center gap-2 mb-0.5">
            <span class="text-xs font-semibold text-yellow-700 bg-yellow-200 px-2 py-0.5 rounded-full">Próximo</span>
            <span class="text-xs text-yellow-600 font-mono">{{ formatHora(proximoTurno.fechaHora) }}</span>
          </div>
          <p class="font-semibold truncate">{{ proximoTurno.pacienteApellido }}, {{ proximoTurno.pacienteNombre }}</p>
          <p class="text-sm text-yellow-700 truncate">{{ proximoTurno.estudioNombre ?? "Sin estudio asignado" }}</p>
          <p class="text-xs text-yellow-500 truncate">{{ proximoTurno.sucursalNombre }}
            <span v-if="proximoTurno.obraSocialNombre"> · {{ proximoTurno.obraSocialNombre }}</span>
          </p>
        </div>
      </div>
    </div>

    <!-- Lista completa de turnos del día -->
    <Card>
      <CardHeader class="pb-2">
        <div class="flex items-center justify-between">
          <CardTitle class="flex items-center gap-2 text-base">
            <Calendar class="h-4 w-4 text-primary" /> Agenda de hoy
          </CardTitle>
          <button @click="router.push('/dashboard/mi-agenda')"
            class="text-xs text-primary hover:underline flex items-center gap-1">
            Ver calendario completo <ChevronRight class="h-3 w-3" />
          </button>
        </div>
      </CardHeader>
      <CardContent class="p-0">

        <!-- Loading skeleton -->
        <div v-if="loading" class="divide-y">
          <div v-for="i in 4" :key="i" class="px-4 py-3 flex items-center gap-3 animate-pulse">
            <div class="w-12 h-4 bg-muted rounded"></div>
            <div class="flex-1 h-4 bg-muted rounded"></div>
            <div class="w-20 h-4 bg-muted rounded"></div>
          </div>
        </div>

        <!-- Empty state -->
        <div v-else-if="turnos.length === 0" class="py-10 text-center text-muted-foreground">
          <Calendar class="h-8 w-8 mx-auto mb-2 opacity-40" />
          <p class="text-sm">No tenés turnos asignados para hoy</p>
        </div>

        <!-- Tabla de turnos -->
        <div v-else class="divide-y text-sm">
          <div
            v-for="t in turnos" :key="t.id"
            :class="['flex items-center gap-3 px-4 py-3 transition-colors',
              isCurrent(t) ? 'bg-blue-50 border-l-4 border-l-blue-400' :
              isNext(t)    ? 'bg-yellow-50 border-l-4 border-l-yellow-400' :
              estadoCfg[t.estado]?.row ?? '']">

            <!-- Hora -->
            <span class="w-12 font-mono font-semibold shrink-0 text-xs">
              {{ formatHora(t.fechaHora) }}
            </span>

            <!-- Dot estado -->
            <span :class="['w-2 h-2 rounded-full shrink-0', estadoCfg[t.estado]?.dot ?? 'bg-gray-300']"></span>

            <!-- Paciente + estudio -->
            <div class="flex-1 min-w-0">
              <div class="font-medium truncate flex items-center gap-1.5">
                <UserRound class="h-3.5 w-3.5 text-muted-foreground shrink-0" />
                {{ t.pacienteApellido }}, {{ t.pacienteNombre }}
                <span v-if="isCurrent(t)"
                  class="inline-flex items-center gap-1 text-[10px] font-semibold text-blue-700 bg-blue-200 px-1.5 py-0.5 rounded-full ml-1">
                  <span class="w-1 h-1 bg-blue-600 rounded-full animate-pulse"></span> En curso
                </span>
                <span v-else-if="isNext(t)"
                  class="text-[10px] font-semibold text-yellow-700 bg-yellow-200 px-1.5 py-0.5 rounded-full ml-1">
                  Próximo
                </span>
              </div>
              <div class="text-xs text-muted-foreground truncate flex items-center gap-1.5 mt-0.5">
                <Stethoscope class="h-3 w-3 shrink-0" />
                {{ t.estudioNombre ?? "Sin estudio" }}
                <span v-if="t.obraSocialNombre" class="opacity-70">· {{ t.obraSocialNombre }}</span>
              </div>
            </div>

            <!-- Sucursal -->
            <span class="text-xs px-1.5 py-0.5 rounded bg-muted shrink-0 hidden sm:inline">
              {{ t.sucursalNombre }}
            </span>

            <!-- Estado -->
            <span class="text-xs shrink-0 font-medium" :class="{
              'text-yellow-600': t.estado === 'PENDIENTE',
              'text-blue-600':   t.estado === 'CONFIRMADO',
              'text-green-600':  t.estado === 'ATENDIDO',
              'text-red-500':    t.estado === 'CANCELADO',
              'text-gray-400':   t.estado === 'AUSENTE',
            }">
              {{ estadoCfg[t.estado]?.label ?? t.estado }}
            </span>
          </div>
        </div>

      </CardContent>
    </Card>

  </div>
</template>
