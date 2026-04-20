<script setup lang="ts">
import { ref, onMounted } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";
import PaginationControls from "@/components/ui/PaginationControls.vue";
import { Plus, X, ChevronDown, ChevronRight, Building2, Search } from "lucide-vue-next";

const API = "http://localhost:8080/api/obras-sociales";
const auth = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

interface PlanDto { id: number; nombre: string; obraSocialId: number; activo: boolean; }
interface OsDto { id: number; nombre: string; codigo: string | null; telefono: string | null; email: string | null; activa: boolean; planes: PlanDto[]; }

const obrasSociales = ref<OsDto[]>([]);
const loading = ref(true);
const globalError = ref("");
const searchQuery = ref("");
const currentPage = ref(0);
const pageSize = 20;
const totalPages = ref(0);
const totalElements = ref(0);

const showNewForm = ref(false);
const newForm = ref({ nombre: "", codigo: "", telefono: "", email: "" });
const newSaving = ref(false);
const newError = ref("");

const editingId = ref<number | null>(null);
const editForm = ref({ nombre: "", codigo: "", telefono: "", email: "" });
const editSaving = ref(false);
const editError = ref("");

const expandedId = ref<number | null>(null);
const newPlanName = ref("");
const newPlanSaving = ref(false);

const confirmDeleteId = ref<number | null>(null);

async function loadPage(page: number) {
  loading.value = true;
  currentPage.value = page;
  try {
    const q = searchQuery.value.trim();
    const params = new URLSearchParams({ page: String(page), size: String(pageSize) });
    if (q) params.set("q", q);
    const res = await axios.get(`${API}?${params}`, { headers: headers() });
    obrasSociales.value = res.data.content;
    totalPages.value = res.data.totalPages;
    totalElements.value = res.data.totalElements;
  } catch { globalError.value = "Error al cargar obras sociales."; }
  finally { loading.value = false; }
}

async function loadData() { await loadPage(0); }

onMounted(loadData);

async function createOs() {
  newError.value = "";
  if (!newForm.value.nombre) { newError.value = "El nombre es obligatorio."; return; }
  newSaving.value = true;
  try {
    await axios.post(API, newForm.value, { headers: headers() });
    newForm.value = { nombre: "", codigo: "", telefono: "", email: "" };
    showNewForm.value = false;
    await loadData();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear.";
  } finally { newSaving.value = false; }
}

function startEdit(os: OsDto) {
  editingId.value = os.id;
  editForm.value = { nombre: os.nombre, codigo: os.codigo ?? "", telefono: os.telefono ?? "", email: os.email ?? "" };
  editError.value = "";
}

async function saveEdit(os: OsDto) {
  editSaving.value = true;
  try {
    await axios.put(`${API}/${os.id}`, editForm.value, { headers: headers() });
    editingId.value = null;
    await loadData();
  } catch (e: any) {
    editError.value = e?.response?.data?.error ?? "Error al guardar.";
  } finally { editSaving.value = false; }
}

async function deleteOs(id: number) {
  try {
    await axios.delete(`${API}/${id}`, { headers: headers() });
    await loadData();
    confirmDeleteId.value = null;
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al eliminar.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  }
}

function toggleExpand(id: number) {
  expandedId.value = expandedId.value === id ? null : id;
  newPlanName.value = "";
}

async function addPlan(osId: number) {
  if (!newPlanName.value.trim()) return;
  newPlanSaving.value = true;
  try {
    await axios.post(`${API}/${osId}/planes`, { nombre: newPlanName.value }, { headers: headers() });
    newPlanName.value = "";
    await loadData();
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al crear plan.";
  } finally { newPlanSaving.value = false; }
}

async function deletePlan(planId: number) {
  try {
    await axios.delete(`${API}/planes/${planId}`, { headers: headers() });
    await loadData();
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al eliminar plan.";
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">Obras Sociales y Planes</h1>
        <p class="text-muted-foreground text-sm">Gestión de obras sociales y sus planes</p>
      </div>
      <button @click="showNewForm = !showNewForm" class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors">
        <component :is="showNewForm ? X : Plus" class="h-4 w-4" />
        {{ showNewForm ? "Cancelar" : "Nueva Obra Social" }}
      </button>
    </div>

    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ globalError }}</div>

    <!-- Search -->
    <div class="flex gap-2">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
        <Input v-model="searchQuery" placeholder="Buscar obra social..." class="pl-9" @keyup.enter="loadData" />
      </div>
      <Button variant="outline" @click="loadData">Buscar</Button>
    </div>

    <!-- New OS Form -->
    <Card v-if="showNewForm">
      <CardHeader><CardTitle class="text-base">Nueva Obra Social</CardTitle></CardHeader>
      <CardContent>
        <form @submit.prevent="createOs" class="space-y-4">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-sm font-medium">Nombre <span class="text-destructive">*</span></label>
              <Input v-model="newForm.nombre" placeholder="Nombre de la obra social" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Código</label>
              <Input v-model="newForm.codigo" placeholder="Código identificador" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Teléfono</label>
              <Input v-model="newForm.telefono" placeholder="0810-XXX-XXXX" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Email</label>
              <Input v-model="newForm.email" type="email" placeholder="contacto@ejemplo.com" />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Crear Obra Social" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- OS List -->
    <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando...</div>
    <div v-else-if="obrasSociales.length === 0" class="p-6 text-center text-muted-foreground">
      <Building2 class="h-8 w-8 mx-auto mb-2 opacity-50" />
      <p class="text-sm">No hay obras sociales registradas</p>
    </div>
    <div v-else class="space-y-3">

      <Card v-for="os in obrasSociales" :key="os.id">
        <CardContent class="p-0">
          <!-- OS Header Row -->
          <div v-if="editingId !== os.id" class="flex items-center justify-between px-4 py-3">
            <div class="flex items-center gap-3 cursor-pointer" @click="toggleExpand(os.id)">
              <component :is="expandedId === os.id ? ChevronDown : ChevronRight" class="h-4 w-4 text-muted-foreground" />
              <div>
                <div class="font-medium flex items-center gap-2">
                  {{ os.nombre }}
                  <span v-if="os.codigo" class="text-xs px-2 py-0.5 rounded-full bg-muted text-muted-foreground">{{ os.codigo }}</span>
                </div>
                <div class="text-xs text-muted-foreground">
                  {{ os.telefono ?? "" }} {{ os.email ? `· ${os.email}` : "" }}
                  · {{ os.planes.length }} plan{{ os.planes.length !== 1 ? 'es' : '' }}
                </div>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <div v-if="confirmDeleteId === os.id" class="flex items-center gap-2">
                <span class="text-xs text-muted-foreground">¿Eliminar?</span>
                <button @click="deleteOs(os.id)" class="text-xs px-2 py-1 rounded bg-destructive text-destructive-foreground hover:bg-destructive/90">Sí</button>
                <button @click="confirmDeleteId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">No</button>
              </div>
              <template v-else>
                <button @click="startEdit(os)" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Editar</button>
                <button @click="confirmDeleteId = os.id" class="text-xs px-3 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Eliminar</button>
              </template>
            </div>
          </div>
          <!-- Edit Row -->
          <div v-else class="px-4 py-3 bg-muted/20 space-y-3">
            <div v-if="editError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-2 text-xs text-destructive">{{ editError }}</div>
            <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
              <Input v-model="editForm.nombre" placeholder="Nombre" class="h-8 text-sm" />
              <Input v-model="editForm.codigo" placeholder="Código" class="h-8 text-sm" />
              <Input v-model="editForm.telefono" placeholder="Teléfono" class="h-8 text-sm" />
              <Input v-model="editForm.email" placeholder="Email" class="h-8 text-sm" />
            </div>
            <div class="flex justify-end gap-2">
              <button @click="saveEdit(os)" :disabled="editSaving" class="text-xs px-3 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90">{{ editSaving ? "..." : "Guardar" }}</button>
              <button @click="editingId = null" class="text-xs px-3 py-1 rounded border hover:bg-muted">Cancelar</button>
            </div>
          </div>
          <!-- Planes (expanded) -->
          <div v-if="expandedId === os.id" class="border-t px-4 py-3 bg-muted/10">
            <div class="text-xs font-medium text-muted-foreground mb-2 uppercase tracking-wider">Planes</div>
            <div class="space-y-1 mb-3">
              <div v-for="plan in os.planes" :key="plan.id" class="flex items-center justify-between py-1.5 px-2 rounded hover:bg-muted/50">
                <span class="text-sm">{{ plan.nombre }}</span>
                <button @click="deletePlan(plan.id)" class="text-xs text-destructive hover:underline">Quitar</button>
              </div>
              <div v-if="os.planes.length === 0" class="text-xs text-muted-foreground py-1">Sin planes registrados</div>
            </div>
            <div class="flex gap-2">
              <Input v-model="newPlanName" placeholder="Nombre del nuevo plan" class="h-8 text-sm flex-1" @keyup.enter="addPlan(os.id)" />
              <button @click="addPlan(os.id)" :disabled="newPlanSaving" class="text-xs px-3 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90">
                {{ newPlanSaving ? "..." : "+ Plan" }}
              </button>
            </div>
          </div>
        </CardContent>
      </Card>
      <PaginationControls
        :page="currentPage"
        :total-pages="totalPages"
        :total-elements="totalElements"
        :size="pageSize"
        :loading="loading"
        @change="loadPage"
      />
    </div>
  </div>
</template>
