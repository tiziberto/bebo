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
import Select from "@/components/ui/Select.vue";
import PaginationControls from "@/components/ui/PaginationControls.vue";
import { Search, Plus, X, Users } from "lucide-vue-next";

const API = "http://localhost:8080/api";
const auth = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

interface PacienteDto {
  id: number; nombre: string; apellido: string; dni: string;
  fechaNacimiento: string | null; sexo: string | null;
  telefono: string | null; email: string | null; direccion: string | null;
  obraSocialId: number | null; obraSocialNombre: string | null;
  planId: number | null; planNombre: string | null;
  numeroAfiliado: string | null; observaciones: string | null; activo: boolean;
}
interface OsOption { value: string; label: string; }

const pacientes = ref<PacienteDto[]>([]);
const loading = ref(true);
const searchQuery = ref("");
const globalError = ref("");
const currentPage = ref(0);
const pageSize = 20;
const totalPages = ref(0);
const totalElements = ref(0);

const obrasSociales = ref<{ id: number; nombre: string }[]>([]);

const showNewForm = ref(false);
const newForm = ref({ nombre: "", apellido: "", dni: "", fechaNacimiento: "", sexo: "", telefono: "", email: "", direccion: "", obraSocialId: "", numeroAfiliado: "", observaciones: "" });
const newSaving = ref(false);
const newError = ref("");

const editingId = ref<number | null>(null);
const editForm = ref({ nombre: "", apellido: "", dni: "", fechaNacimiento: "", sexo: "", telefono: "", email: "", direccion: "", obraSocialId: "", numeroAfiliado: "", observaciones: "" });
const editSaving = ref(false);
const editError = ref("");

const confirmDeleteId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

const sexoOptions = [
  { value: "MASCULINO", label: "Masculino" },
  { value: "FEMENINO", label: "Femenino" },
  { value: "OTRO", label: "Otro" },
];

const osOptions = computed<OsOption[]>(() => [
  { value: "", label: "Sin obra social" },
  ...obrasSociales.value.map(os => ({ value: String(os.id), label: os.nombre }))
]);

async function loadPage(page: number) {
  loading.value = true;
  globalError.value = "";
  currentPage.value = page;
  try {
    const q = searchQuery.value.trim();
    const params = new URLSearchParams({ page: String(page), size: String(pageSize) });
    if (q) params.set("q", q);
    const [pacRes, osRes] = await Promise.all([
      axios.get(`${API}/pacientes?${params}`, { headers: headers() }),
      page === 0 ? axios.get(`${API}/obras-sociales?size=1000`, { headers: headers() }) : Promise.resolve(null),
    ]);
    const pageData = pacRes.data;
    pacientes.value = pageData.content;
    totalPages.value = pageData.totalPages;
    totalElements.value = pageData.totalElements;

    if (osRes) {
      const osData: typeof obrasSociales.value = osRes.data.content ?? osRes.data;
      obrasSociales.value = osData;
    }
  } catch {
    globalError.value = "Error al cargar datos.";
  } finally {
    loading.value = false;
  }
}

async function loadData() { await loadPage(0); }

onMounted(loadData);

async function searchServer() {
  await loadPage(0);
}

async function createPaciente() {
  newError.value = "";
  if (!newForm.value.nombre || !newForm.value.apellido || !newForm.value.dni) {
    newError.value = "Nombre, apellido y DNI son obligatorios.";
    return;
  }
  if (!newForm.value.fechaNacimiento || !newForm.value.sexo || !newForm.value.telefono || !newForm.value.email) {
    newError.value = "Fecha de nacimiento, sexo, teléfono y email son obligatorios.";
    return;
  }
  newSaving.value = true;
  try {
    await axios.post(`${API}/pacientes`, {
      ...newForm.value,
      obraSocialId: newForm.value.obraSocialId ? Number(newForm.value.obraSocialId) : null,
    }, { headers: headers() });
    newForm.value = { nombre: "", apellido: "", dni: "", fechaNacimiento: "", sexo: "", telefono: "", email: "", direccion: "", obraSocialId: "", numeroAfiliado: "", observaciones: "" };
    showNewForm.value = false;
    await loadData();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear paciente.";
  } finally { newSaving.value = false; }
}

function startEdit(p: PacienteDto) {
  editingId.value = p.id;
  editForm.value = {
    nombre: p.nombre, apellido: p.apellido, dni: p.dni,
    fechaNacimiento: p.fechaNacimiento ?? "", sexo: p.sexo ?? "",
    telefono: p.telefono ?? "", email: p.email ?? "", direccion: p.direccion ?? "",
    obraSocialId: p.obraSocialId ? String(p.obraSocialId) : "",
    numeroAfiliado: p.numeroAfiliado ?? "", observaciones: p.observaciones ?? "",
  };
  editError.value = "";
  confirmDeleteId.value = null;
}

async function saveEdit(p: PacienteDto) {
  editError.value = "";
  if (!editForm.value.fechaNacimiento || !editForm.value.sexo || !editForm.value.telefono || !editForm.value.email) {
    editError.value = "Fecha de nacimiento, sexo, teléfono y email son obligatorios.";
    return;
  }
  editSaving.value = true;
  try {
    await axios.put(`${API}/pacientes/${p.id}`, {
      ...editForm.value,
      obraSocialId: editForm.value.obraSocialId ? Number(editForm.value.obraSocialId) : null,
    }, { headers: headers() });
    editingId.value = null;
    await loadData();
  } catch (e: any) {
    editError.value = e?.response?.data?.error ?? "Error al guardar.";
  } finally { editSaving.value = false; }
}

async function deletePaciente(id: number) {
  deletingId.value = id;
  try {
    await axios.delete(`${API}/pacientes/${id}`, { headers: headers() });
    pacientes.value = pacientes.value.filter(p => p.id !== id);
    confirmDeleteId.value = null;
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al eliminar.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  } finally { deletingId.value = null; }
}
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">Pacientes</h1>
        <p class="text-muted-foreground text-sm">Registro y gestión de pacientes</p>
      </div>
      <button
        @click="showNewForm = !showNewForm"
        class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors"
      >
        <component :is="showNewForm ? X : Plus" class="h-4 w-4" />
        {{ showNewForm ? "Cancelar" : "Nuevo Paciente" }}
      </button>
    </div>

    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ globalError }}</div>

    <!-- Search -->
    <div class="flex gap-2">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
        <Input v-model="searchQuery" placeholder="Buscar por nombre, apellido, DNI o email..." class="pl-9" @keyup.enter="searchServer" />
      </div>
      <Button variant="outline" @click="searchServer">Buscar</Button>
    </div>

    <!-- New Patient Form -->
    <Card v-if="showNewForm">
      <CardHeader><CardTitle class="text-base">Registrar nuevo paciente</CardTitle></CardHeader>
      <CardContent>
        <form @submit.prevent="createPaciente" class="space-y-4">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            <div class="space-y-1">
              <label class="text-sm font-medium">Apellido <span class="text-destructive">*</span></label>
              <Input v-model="newForm.apellido" placeholder="Apellido" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Nombre <span class="text-destructive">*</span></label>
              <Input v-model="newForm.nombre" placeholder="Nombre" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">DNI <span class="text-destructive">*</span></label>
              <Input v-model="newForm.dni" placeholder="12345678" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Fecha de Nacimiento <span class="text-destructive">*</span></label>
              <Input v-model="newForm.fechaNacimiento" type="date" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Sexo <span class="text-destructive">*</span></label>
              <Select v-model="newForm.sexo" :options="sexoOptions" placeholder="Seleccione..." required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Teléfono <span class="text-destructive">*</span></label>
              <Input v-model="newForm.telefono" placeholder="011-1234-5678" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Email <span class="text-destructive">*</span></label>
              <Input v-model="newForm.email" type="email" placeholder="correo@ejemplo.com" required />
            </div>
            <div class="space-y-1 sm:col-span-2">
              <label class="text-sm font-medium">Dirección</label>
              <Input v-model="newForm.direccion" placeholder="Dirección completa" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Obra Social</label>
              <Select v-model="newForm.obraSocialId" :options="osOptions" placeholder="Sin obra social" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">N° Afiliado</label>
              <Input v-model="newForm.numeroAfiliado" placeholder="Número de afiliado" />
            </div>
          </div>
          <div class="space-y-1">
            <label class="text-sm font-medium">Observaciones</label>
            <Input v-model="newForm.observaciones" placeholder="Notas adicionales..." />
          </div>
          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Registrar Paciente" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Patients Table -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando...</div>
        <div v-else-if="pacientes.length === 0" class="p-6 text-center text-muted-foreground">
          <Users class="h-8 w-8 mx-auto mb-2 opacity-50" />
          <p class="text-sm">No se encontraron pacientes</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="border-b bg-muted/50">
              <tr>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Paciente</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">DNI</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Contacto</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Obra Social</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Plan</th>
                <th class="px-4 py-3 text-right font-medium text-muted-foreground">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="p in pacientes" :key="p.id">
                <tr v-if="editingId !== p.id" class="border-b hover:bg-muted/30 transition-colors">
                  <td class="px-4 py-3 font-medium">{{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}</td>
                  <td class="px-4 py-3 text-muted-foreground">{{ p.dni }}</td>
                  <td class="px-4 py-3 text-muted-foreground">
                    <div>{{ p.telefono ?? "—" }}</div>
                    <div class="text-xs">{{ p.email ?? "" }}</div>
                  </td>
                  <td class="px-4 py-3">
                    <span v-if="p.obraSocialNombre" class="text-xs px-2 py-0.5 rounded-full bg-primary/10 text-primary font-medium">{{ p.obraSocialNombre }}</span>
                    <span v-else class="text-xs text-muted-foreground">—</span>
                  </td>
                  <td class="px-4 py-3 text-muted-foreground text-xs">{{ p.planNombre ?? "—" }}</td>
                  <td class="px-4 py-3 text-right">
                    <div v-if="confirmDeleteId === p.id" class="flex items-center justify-end gap-2">
                      <span class="text-xs text-muted-foreground">¿Dar de baja?</span>
                      <button @click="deletePaciente(p.id)" :disabled="deletingId === p.id" class="text-xs px-2 py-1 rounded bg-destructive text-destructive-foreground hover:bg-destructive/90">Sí</button>
                      <button @click="confirmDeleteId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">No</button>
                    </div>
                    <div v-else class="flex items-center justify-end gap-2">
                      <button @click="startEdit(p)" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Editar</button>
                      <button @click="confirmDeleteId = p.id" class="text-xs px-3 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Baja</button>
                    </div>
                  </td>
                </tr>
                <!-- Edit row -->
                <tr v-else class="border-b bg-muted/20">
                  <td colspan="6" class="px-4 py-4">
                    <div class="space-y-3">
                      <div v-if="editError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-2 text-xs text-destructive">{{ editError }}</div>
                      <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
                        <Input v-model="editForm.apellido" placeholder="Apellido" class="h-8 text-sm" />
                        <Input v-model="editForm.nombre" placeholder="Nombre" class="h-8 text-sm" />
                        <Input v-model="editForm.dni" placeholder="DNI" class="h-8 text-sm" />
                        <Input v-model="editForm.telefono" placeholder="Teléfono" class="h-8 text-sm" />
                        <Input v-model="editForm.email" placeholder="Email" class="h-8 text-sm" />
                        <Select v-model="editForm.obraSocialId" :options="osOptions" class="h-8 text-xs" />
                        <Input v-model="editForm.numeroAfiliado" placeholder="N° Afiliado" class="h-8 text-sm" />
                      </div>
                      <div class="flex justify-end gap-2">
                        <button @click="saveEdit(p)" :disabled="editSaving" class="text-xs px-3 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90 transition-colors">{{ editSaving ? "..." : "Guardar" }}</button>
                        <button @click="editingId = null" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Cancelar</button>
                      </div>
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
