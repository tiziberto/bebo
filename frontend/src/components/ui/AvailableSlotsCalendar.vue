<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import axios from "axios";
import { ChevronLeft, ChevronRight, Clock, Calendar, CalendarDays } from "lucide-vue-next";
import Button from "./Button.vue";

const props = defineProps<{
  profesionalId: number | string;
  sucursalId: number | string;
  headers: Record<string, string>;
  apiUrl: string;
}>();

const emit = defineEmits<{
  selected: [fechaHora: string];
  close: [];
}>();

const API = props.apiUrl || "http://localhost:8080/api";

interface TimeSlot {
  time: string;
  available: boolean;
}

interface DaySlots {
  date: string;
  dayName: string;
  slots: TimeSlot[];
}

interface MonthDay {
  date: string;
  dayNumber: string;
  dayName: string;
  slots: TimeSlot[] | null;
  isCurrentMonth: boolean;
}

type ViewMode = "day" | "week" | "month";

const currentDate = ref(new Date());
const viewMode = ref<ViewMode>("week");
const daysWithSlots = ref<DaySlots[]>([]);
const loading = ref(false);
const error = ref("");
const selectedDateTime = ref<string | null>(null);

const startDate = computed(() => {
  const d = new Date(currentDate.value);
  d.setHours(0, 0, 0, 0);
  return d;
});

// Compute the date range based on view mode
const dateRange = computed(() => {
  const start = new Date(startDate.value);
  const end = new Date(startDate.value);

  if (viewMode.value === "day") {
    return { start, end };
  } else if (viewMode.value === "week") {
    // Normalize to Monday
    const day = start.getDay();
    start.setDate(start.getDate() - (day === 0 ? 6 : day - 1));
    end.setDate(start.getDate() + 6);
    return { start, end };
  } else {
    // Full month
    start.setDate(1);
    end.setMonth(end.getMonth() + 1);
    end.setDate(0);
    return { start, end };
  }
});

// Format dates for API
const apiStartDate = computed(() => dateRange.value.start.toISOString().split("T")[0]);
const apiEndDate = computed(() => dateRange.value.end.toISOString().split("T")[0]);

const weekDays = computed(() => {
  const days: DaySlots[] = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(startDate.value);
    d.setDate(d.getDate() + i);
    const dateStr = d.toISOString().split("T")[0];
    const dayName = d.toLocaleDateString("es-AR", { weekday: "short", day: "2-digit" });
    days.push({ date: dateStr, dayName, slots: [] });
  }
  return days;
});

const monthDays = computed(() => {
  if (daysWithSlots.value.length === 0) return [];

  const days: MonthDay[] = [];
  const firstDay = new Date(dateRange.value.start);
  const lastDay = new Date(dateRange.value.end);

  // Start from the beginning of the calendar grid (previous month days if needed)
  const startDate = new Date(firstDay);
  startDate.setDate(startDate.getDate() - startDate.getDay());

  for (let d = new Date(startDate); d <= lastDay; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split("T")[0];
    const dayData = daysWithSlots.value.find((ds) => ds.date === dateStr);
    const isCurrentMonth = d.getMonth() === new Date(currentDate.value).getMonth();

    days.push({
      date: dateStr,
      dayNumber: d.getDate().toString(),
      dayName: d.toLocaleDateString("es-AR", { weekday: "short" }),
      slots: dayData?.slots || null,
      isCurrentMonth,
    });
  }

  return days;
});

const daysToShow = computed(() => daysWithSlots.value || weekDays.value);

async function loadAvailableSlots() {
  if (!props.profesionalId || !props.sucursalId) return;
  loading.value = true;
  error.value = "";
  try {
    const res = await axios.get(
      `${API}/agenda-medica/disponibles?profesionalId=${props.profesionalId}&sucursalId=${props.sucursalId}&desde=${apiStartDate.value}&hasta=${apiEndDate.value}`,
      { headers: props.headers }
    );

    daysWithSlots.value = res.data.map((day: any) => ({
      date: day.date,
      dayName: new Date(day.date).toLocaleDateString("es-AR", { weekday: "short", day: "2-digit" }),
      slots: day.slots.map((slot: any) => ({
        time: slot.time,
        available: slot.available,
      })),
    }));
  } catch (e: any) {
    error.value = e?.response?.data?.error ?? "Error al cargar horarios disponibles";
    daysWithSlots.value = weekDays.value;
  } finally {
    loading.value = false;
  }
}

function navigate(direction: 1 | -1) {
  const d = new Date(currentDate.value);

  if (viewMode.value === "day") {
    d.setDate(d.getDate() + direction);
  } else if (viewMode.value === "week") {
    d.setDate(d.getDate() + direction * 7);
  } else {
    // month
    d.setMonth(d.getMonth() + direction);
  }

  currentDate.value = d;
}

function selectSlot(date: string, time: string) {
  const [hours, minutes] = time.split(":").map(Number);
  const d = new Date(date);
  d.setHours(hours, minutes, 0);
  const isoString = d.toISOString().split(".")[0];
  selectedDateTime.value = isoString;
  emit("selected", isoString);
}

function handleClose() {
  emit("close");
}

watch([() => props.profesionalId, () => props.sucursalId, apiStartDate, apiEndDate], loadAvailableSlots);

onMounted(loadAvailableSlots);
</script>

<template>
  <div class="space-y-4">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <h3 class="text-sm font-semibold">Horarios Disponibles</h3>
      <button @click="handleClose" class="text-xs text-muted-foreground hover:text-foreground">Cerrar</button>
    </div>

    <!-- Error -->
    <div v-if="error" class="rounded-md bg-destructive/10 border border-destructive/30 px-3 py-2 text-xs text-destructive">
      {{ error }}
    </div>

    <!-- Loading -->
    <div v-if="loading" class="text-xs text-muted-foreground text-center py-4">Cargando horarios...</div>

    <!-- Main Content -->
    <div v-else class="space-y-3">
      <!-- View Mode Toggle -->
      <div class="flex gap-2 border-b">
        <button
          @click="viewMode = 'day'"
          :class="[
            'px-3 py-2 text-sm font-medium transition-colors flex items-center gap-2',
            viewMode === 'day'
              ? 'text-primary border-b-2 border-primary'
              : 'text-muted-foreground hover:text-foreground',
          ]"
        >
          <Calendar class="h-4 w-4" />
          Día
        </button>
        <button
          @click="viewMode = 'week'"
          :class="[
            'px-3 py-2 text-sm font-medium transition-colors flex items-center gap-2',
            viewMode === 'week'
              ? 'text-primary border-b-2 border-primary'
              : 'text-muted-foreground hover:text-foreground',
          ]"
        >
          <CalendarDays class="h-4 w-4" />
          Semana
        </button>
        <button
          @click="viewMode = 'month'"
          :class="[
            'px-3 py-2 text-sm font-medium transition-colors flex items-center gap-2',
            viewMode === 'month'
              ? 'text-primary border-b-2 border-primary'
              : 'text-muted-foreground hover:text-foreground',
          ]"
        >
          <Calendar class="h-4 w-4" />
          Mes
        </button>
      </div>

      <!-- Navigation -->
      <div class="flex items-center justify-between">
        <button @click="navigate(-1)" class="p-1 hover:bg-muted rounded">
          <ChevronLeft class="h-4 w-4" />
        </button>
        <span class="text-xs font-medium">
          {{
            viewMode === 'day'
              ? new Date(currentDate).toLocaleDateString('es-AR', { weekday: 'long', day: '2-digit', month: 'long' })
              : viewMode === 'week'
                ? `Semana del ${dateRange.start.toLocaleDateString('es-AR', { day: '2-digit', month: 'short' })}`
                : new Date(currentDate).toLocaleDateString('es-AR', { month: 'long', year: 'numeric' })
          }}
        </span>
        <button @click="navigate(1)" class="p-1 hover:bg-muted rounded">
          <ChevronRight class="h-4 w-4" />
        </button>
      </div>

      <!-- Day View -->
      <div v-if="viewMode === 'day'" class="space-y-3">
        <div v-if="daysToShow.length === 0 || daysToShow[0].slots.length === 0" class="text-xs text-muted-foreground text-center p-4 bg-muted/20 rounded">
          Sin horarios disponibles para este día
        </div>
        <div v-else class="flex flex-wrap gap-2">
          <button
            v-for="slot in daysToShow[0].slots"
            :key="`${daysToShow[0].date}-${slot.time}`"
            @click="selectSlot(daysToShow[0].date, slot.time)"
            :disabled="!slot.available"
            :class="[
              'px-4 py-2 rounded border text-sm transition-colors flex items-center gap-2',
              slot.available
                ? 'border-primary/30 hover:bg-primary/10 cursor-pointer font-medium text-primary'
                : 'border-input bg-muted text-muted-foreground cursor-not-allowed opacity-50',
              selectedDateTime === `${daysToShow[0].date}T${slot.time}:00`
                ? 'bg-primary text-primary-foreground border-primary'
                : '',
            ]"
          >
            <Clock class="h-4 w-4" />
            {{ slot.time }}
          </button>
        </div>
      </div>

      <!-- Week View -->
      <div v-else-if="viewMode === 'week'" class="grid grid-cols-7 gap-1">
        <div v-for="day in daysToShow" :key="day.date" class="space-y-1.5">
          <!-- Day Header -->
          <div class="text-xs font-medium text-center text-muted-foreground p-1">
            {{ day.dayName }}
          </div>

          <!-- Slots -->
          <div v-if="day.slots.length > 0" class="space-y-0.5 max-h-64 overflow-y-auto">
            <button
              v-for="slot in day.slots"
              :key="`${day.date}-${slot.time}`"
              @click="selectSlot(day.date, slot.time)"
              :disabled="!slot.available"
              :class="[
                'w-full text-xs py-1.5 px-1 rounded border transition-colors flex items-center justify-center gap-1',
                slot.available
                  ? 'border-primary/30 hover:bg-primary/10 cursor-pointer font-medium text-primary'
                  : 'border-input bg-muted text-muted-foreground cursor-not-allowed opacity-50',
                selectedDateTime === `${day.date}T${slot.time}:00`
                  ? 'bg-primary text-primary-foreground border-primary'
                  : '',
              ]"
            >
              <Clock class="h-3 w-3" />
              {{ slot.time }}
            </button>
          </div>

          <!-- No Slots -->
          <div v-else class="text-xs text-muted-foreground text-center p-2 bg-muted/20 rounded">
            Sin horarios
          </div>
        </div>
      </div>

      <!-- Month View -->
      <div v-else-if="viewMode === 'month'" class="grid grid-cols-7 gap-1">
        <div
          v-for="day in monthDays"
          :key="day.date"
          :class="[
            'border rounded p-2 min-h-24 text-xs',
            day.isCurrentMonth ? 'bg-white' : 'bg-muted/30',
          ]"
        >
          <!-- Day Number -->
          <div :class="['font-semibold mb-1 text-center', day.isCurrentMonth ? 'text-foreground' : 'text-muted-foreground']">
            {{ day.dayNumber }}
          </div>

          <!-- Slots -->
          <div v-if="day.slots && day.slots.length > 0" class="flex flex-col gap-0.5">
            <button
              v-for="slot in day.slots.slice(0, 2)"
              :key="`${day.date}-${slot.time}`"
              @click="selectSlot(day.date, slot.time)"
              :disabled="!slot.available"
              :class="[
                'text-xs px-1 py-0.5 rounded border text-center transition-colors',
                slot.available
                  ? 'border-primary/30 hover:bg-primary/10 cursor-pointer font-medium text-primary'
                  : 'border-input bg-muted text-muted-foreground cursor-not-allowed opacity-50',
                selectedDateTime === `${day.date}T${slot.time}:00`
                  ? 'bg-primary text-primary-foreground border-primary'
                  : '',
              ]"
            >
              {{ slot.time }}
            </button>
            <div v-if="day.slots.length > 2" class="text-muted-foreground text-center text-xs mt-1">
              +{{ day.slots.length - 2 }}
            </div>
          </div>
          <div v-else class="text-muted-foreground text-center">-</div>
        </div>
      </div>

      <!-- Selected Info -->
      <div v-if="selectedDateTime" class="bg-primary/10 border border-primary/30 rounded-md p-3 text-xs">
        <p class="text-primary font-medium">
          ✓ Horario seleccionado:
          <span class="font-semibold">{{ new Date(selectedDateTime).toLocaleString('es-AR') }}</span>
        </p>
      </div>
    </div>
  </div>
</template>
