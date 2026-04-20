<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import { useAuthStore } from "@/stores/auth";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Input from "@/components/ui/Input.vue";
import Button from "@/components/ui/Button.vue";
import PaginationControls from "@/components/ui/PaginationControls.vue";
import { Plus, X, Search, ClipboardList } from "lucide-vue-next";

const API = "http://localhost:8080/api/estudios";
const auth = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

interface EstudioDto { id: number; nombre: string; codigo: string | null; descripcion: string | null; activo: boolean; }

const estudios = ref<EstudioDto[]>([]);
const loading = ref(true);
const globalError = ref("");
const searchQuery = ref("");
const currentPage = ref(0);
const pageSize = 20;
const totalPages = ref(0);
const totalElements = ref(0);

const showNewForm = ref(false);
const newForm = ref({ nombre: "", codigo: "", descripcion: "" });
const newSaving = ref(false);
const newError = ref("");

const editingId = ref<number | null>(null);
const editForm = ref({ nombre: "", codigo: "", descripcion: "" });
const editSaving = ref(false);
const editError = ref("");
const confirmDeleteId = ref<number | null>(null);

const isAdmin = computed(() => auth.hasAnyRole(["ROLE_ADMIN"]));

async function loadPage(page: number) {
  loading.value = true;
  currentPage.value = page;
  try {
    const q = searchQuery.value.trim();
    const params = new URLSearchParams({ page: String(page), size: String(pageSize) });
    if (q) params.set("q", q);
    const res = await axios.get(`${API}?${params}`, { headers: headers() });
    estudios.value = res.data.content;
    totalPages.value = res.data.totalPages;
    totalElements.value = res.data.totalElements;
  } catch { globalError.value = "Error al cargar estudios."; }
  finally { loading.value = false; }
}

async function loadData() { await loadPage(0); }

onMounted(loadData);

async function createEstudio() {
  newError.value = "";
  if (!newForm.value.nombre) { newError.value = "El nombre es obligatorio."; return; }
  newSaving.value = true;
  try {
    await axios.post(API, newForm.value, { headers: headers() });
    newForm.value = { nombre: "", codigo: "", descripcion: "" };
    showNewForm.value = false;
    await loadData();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear.";
  } finally { newSaving.value = false; }
}

function startEdit(e: EstudioDto) {
  editingId.value = e.id;
  editForm.value = { nombre: e.nombre, codigo: e.codigo ?? "", descripcion: e.descripcion ?? "" };
  editError.value = "";
}

async function saveEdit(e: EstudioDto) {
  editSaving.value = true;
  try {
    await axios.put(`${API}/${e.id}`, editForm.value, { headers: headers() });
    editingId.value = null;
    await loadData();
  } catch (err: any) {
    editError.value = err?.response?.data?.error ?? "Error al guardar.";
  } finally { editSaving.value = false; }
}

async function deleteEstudio(id: number) {
  try {
    await axios.delete(`${API}/${id}`, { headers: headers() });
    await loadData();
    confirmDeleteId.value = null;
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al eliminar.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">Estudios y Procedimientos</h1>
        <p class="text-muted-foreground text-sm">Catálogo de tipos de estudio e imagen que realiza la clínica</p>
      </div>
      <button v-if="isAdmin" @click="showNewForm = !showNewForm"
        class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors">
        <component :is="showNewForm ? X : Plus" class="h-4 w-4" />
        {{ showNewForm ? "Cancelar" : "Nuevo Estudio" }}
      </button>
    </div>

    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ globalError }}</div>

    <!-- Search -->
    <div class="flex gap-2">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
        <Input v-model="searchQuery" placeholder="Buscar por nombre o código..." class="pl-9" @keyup.enter="loadData" />
      </div>
      <Button variant="outline" @click="loadData">Buscar</Button>
    </div>

    <!-- New Form -->
    <Card v-if="showNewForm">
      <CardHeader><CardTitle class="text-base">Nuevo tipo de estudio</CardTitle></CardHeader>
      <CardContent>
        <form @submit.prevent="createEstudio" class="space-y-4">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="space-y-1 sm:col-span-2">
              <label class="text-sm font-medium">Nombre <span class="text-destructive">*</span></label>
              <Input v-model="newForm.nombre" placeholder="Ej: Ecografía Abdominal" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Código</label>
              <Input v-model="newForm.codigo" placeholder="Ej: ECO-ABD" />
            </div>
            <div class="space-y-1 sm:col-span-3">
              <label class="text-sm font-medium">Descripción</label>
              <Input v-model="newForm.descripcion" placeholder="Descripción del procedimiento..." />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Crear Estudio" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Table -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando...</div>
        <div v-else-if="estudios.length === 0" class="p-6 text-center text-muted-foreground">
          <ClipboardList class="h-8 w-8 mx-auto mb-2 opacity-50" />
          <p class="text-sm">No se encontraron estudios</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="border-b bg-muted/50">
              <tr>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Código</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Nombre</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Descripción</th>
                <th v-if="isAdmin" class="px-4 py-3 text-right font-medium text-muted-foreground">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="e in estudios" :key="e.id">
                <tr v-if="editingId !== e.id" class="border-b hover:bg-muted/30 transition-colors">
                  <td class="px-4 py-3">
                    <span v-if="e.codigo" class="text-xs px-2 py-0.5 rounded-full bg-muted font-mono font-medium">{{ e.codigo }}</span>
                    <span v-else class="text-xs text-muted-foreground">—</span>
                  </td>
                  <td class="px-4 py-3 font-medium">{{ e.nombre }}</td>
                  <td class="px-4 py-3 text-muted-foreground text-xs">{{ e.descripcion ?? "—" }}</td>
                  <td v-if="isAdmin" class="px-4 py-3 text-right">
                    <div v-if="confirmDeleteId === e.id" class="flex items-center justify-end gap-2">
                      <span class="text-xs text-muted-foreground">¿Eliminar?</span>
                      <button @click="deleteEstudio(e.id)" class="text-xs px-2 py-1 rounded bg-destructive text-destructive-foreground hover:bg-destructive/90">Sí</button>
                      <button @click="confirmDeleteId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">No</button>
                    </div>
                    <div v-else class="flex items-center justify-end gap-2">
                      <button @click="startEdit(e)" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Editar</button>
                      <button @click="confirmDeleteId = e.id" class="text-xs px-3 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Eliminar</button>
                    </div>
                  </td>
                </tr>
                <tr v-else class="border-b bg-muted/20">
                  <td class="px-4 py-2"><Input v-model="editForm.codigo" placeholder="Código" class="h-8 text-sm font-mono" /></td>
                  <td class="px-4 py-2"><Input v-model="editForm.nombre" placeholder="Nombre" class="h-8 text-sm" /></td>
                  <td class="px-4 py-2"><Input v-model="editForm.descripcion" placeholder="Descripción" class="h-8 text-sm" /></td>
                  <td v-if="isAdmin" class="px-4 py-2 text-right">
                    <div class="flex items-center justify-end gap-2">
                      <div v-if="editError" class="text-xs text-destructive mr-2">{{ editError }}</div>
                      <button @click="saveEdit(e)" :disabled="editSaving" class="text-xs px-3 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90">{{ editSaving ? "..." : "Guardar" }}</button>
                      <button @click="editingId = null" class="text-xs px-3 py-1 rounded border hover:bg-muted">Cancelar</button>
                    </div>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
          <PaginationControls
            :page="currentPage"
            :total-pages="totalPages"
            :total-elements="totalElements"
            :size="pageSize"
            :loading="loading"
            @change="loadPage"
          />
        </div>
      </CardContent>
    </Card>
  </div>
</template>
