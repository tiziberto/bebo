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
import { Search, Plus, X, Stethoscope, Check } from "lucide-vue-next";

const API = "http://localhost:8080/api";
const auth = useAuthStore();
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

interface ProfDto {
  id: number; nombre: string; apellido: string; dni: string | null;
  matricula: string | null; telefono: string | null; email: string | null;
  especialidadesIds: number[]; obrasSocialesIds: number[];
  estudiosIds: number[]; sucursalesIds: number[];
  userId: number | null; activo: boolean;
}

const profesionales = ref<ProfDto[]>([]);
const loading = ref(true);
const globalError = ref("");
const searchQuery = ref("");
const currentPage = ref(0);
const pageSize = 20;
const totalPages = ref(0);
const totalElements = ref(0);

const especialidades = ref<{ id: number; nombre: string }[]>([]);
const obrasSociales = ref<{ id: number; nombre: string }[]>([]);
const estudios = ref<{ id: number; nombre: string; codigo: string | null }[]>([]);
const sucursales = ref<{ id: number; nombre: string }[]>([]);

const showNewForm = ref(false);
const newForm = ref({ nombre: "", apellido: "", dni: "", matricula: "", telefono: "", email: "", especialidadesIds: [] as number[], obrasSocialesIds: [] as number[], estudiosIds: [] as number[], sucursalesIds: [] as number[] });
const newSaving = ref(false);
const newError = ref("");

const editingId = ref<number | null>(null);
const editForm = ref({ nombre: "", apellido: "", dni: "", matricula: "", telefono: "", email: "", especialidadesIds: [] as number[], obrasSocialesIds: [] as number[], estudiosIds: [] as number[], sucursalesIds: [] as number[] });
const editSaving = ref(false);
const editError = ref("");
const confirmDeleteId = ref<number | null>(null);
const deletingId = ref<number | null>(null);

// Expand studies section in edit mode
const showEstudios = ref(false);

const isAdmin = computed(() => auth.hasAnyRole(["ROLE_ADMIN"]));
const isAdminOrRecepcion = computed(() => auth.hasAnyRole(["ROLE_ADMIN", "ROLE_RECEPCION"]));

function name(list: { id: number; nombre: string }[], ids: number[]) {
  return ids.map(id => list.find(i => i.id === id)?.nombre ?? "").filter(Boolean);
}

function toggle(arr: number[], val: number) {
  const i = arr.indexOf(val);
  if (i >= 0) arr.splice(i, 1); else arr.push(val);
}

function toggleAll(arr: number[], items: { id: number }[], selectAll: boolean) {
  arr.length = 0;
  if (selectAll) {
    arr.push(...items.map(item => item.id));
  }
}

async function loadPage(page: number) {
  loading.value = true;
  currentPage.value = page;
  try {
    const q = searchQuery.value.trim();
    const params = new URLSearchParams({ page: String(page), size: String(pageSize) });
    if (q) params.set("q", q);
    const promises: Promise<any>[] = [
      axios.get(`${API}/profesionales?${params}`, { headers: headers() }),
    ];
    if (page === 0) {
      promises.push(
        axios.get(`${API}/especialidades`, { headers: headers() }),
        axios.get(`${API}/obras-sociales?size=1000`, { headers: headers() }),
        axios.get(`${API}/estudios?size=1000`, { headers: headers() }),
        axios.get(`${API}/sucursales`, { headers: headers() }),
      );
    }
    const [profRes, espRes, osRes, estRes, sucRes] = await Promise.all(promises);
    const pageData = profRes.data;
    profesionales.value = pageData.content;
    totalPages.value = pageData.totalPages;
    totalElements.value = pageData.totalElements;
    if (espRes) especialidades.value = espRes.data;
    if (osRes) obrasSociales.value = osRes.data.content ?? osRes.data;
    if (estRes) estudios.value = estRes.data.content ?? estRes.data;
    if (sucRes) sucursales.value = sucRes.data;
  } catch { globalError.value = "Error al cargar datos."; }
  finally { loading.value = false; }
}

async function loadData() { await loadPage(0); }

onMounted(loadData);

async function createProf() {
  newError.value = "";
  if (!newForm.value.nombre || !newForm.value.apellido) { newError.value = "Nombre y apellido son obligatorios."; return; }
  if (!newForm.value.dni) { newError.value = "DNI es obligatorio."; return; }
  if (!newForm.value.matricula) { newError.value = "Matrícula es obligatoria."; return; }
  if (!newForm.value.telefono) { newError.value = "Teléfono es obligatorio."; return; }
  if (!newForm.value.email) { newError.value = "Email es obligatorio."; return; }
  if (newForm.value.sucursalesIds.length === 0) { newError.value = "Seleccioná al menos una sucursal."; return; }
  newSaving.value = true;
  try {
    await axios.post(`${API}/profesionales`, newForm.value, { headers: headers() });
    newForm.value = { nombre: "", apellido: "", dni: "", matricula: "", telefono: "", email: "", especialidadesIds: [], obrasSocialesIds: [], estudiosIds: [], sucursalesIds: [] };
    showNewForm.value = false;
    await loadData();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear.";
  } finally { newSaving.value = false; }
}

function startEdit(p: ProfDto) {
  editingId.value = p.id;
  editForm.value = {
    nombre: p.nombre, apellido: p.apellido, dni: p.dni ?? "", matricula: p.matricula ?? "",
    telefono: p.telefono ?? "", email: p.email ?? "",
    especialidadesIds: [...p.especialidadesIds], obrasSocialesIds: [...p.obrasSocialesIds],
    estudiosIds: [...p.estudiosIds], sucursalesIds: [...p.sucursalesIds],
  };
  showEstudios.value = false;
  editError.value = "";
  confirmDeleteId.value = null;
}

async function saveEdit(p: ProfDto) {
  editSaving.value = true;
  try {
    await axios.put(`${API}/profesionales/${p.id}`, editForm.value, { headers: headers() });
    editingId.value = null;
    await loadData();
  } catch (e: any) {
    editError.value = e?.response?.data?.error ?? "Error al guardar.";
  } finally { editSaving.value = false; }
}

async function deleteProf(id: number) {
  deletingId.value = id;
  try {
    await axios.delete(`${API}/profesionales/${id}`, { headers: headers() });
    profesionales.value = profesionales.value.filter(p => p.id !== id);
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
        <h1 class="text-2xl font-bold">Profesionales</h1>
        <p class="text-muted-foreground text-sm">Registro de profesionales, especialidades, estudios y sucursales</p>
      </div>
      <button v-if="isAdmin" @click="showNewForm = !showNewForm"
        class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors">
        <component :is="showNewForm ? X : Plus" class="h-4 w-4" />
        {{ showNewForm ? "Cancelar" : "Nuevo Profesional" }}
      </button>
    </div>

    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ globalError }}</div>

    <!-- Search -->
    <div class="flex gap-2">
      <div class="relative flex-1">
        <Search class="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
        <Input v-model="searchQuery" placeholder="Buscar por nombre o matrícula..." class="pl-9" @keyup.enter="loadData" />
      </div>
      <Button variant="outline" @click="loadData">Buscar</Button>
    </div>

    <!-- New Form -->
    <Card v-if="showNewForm">
      <CardHeader><CardTitle class="text-base">Registrar nuevo profesional</CardTitle></CardHeader>
      <CardContent>
        <form @submit.prevent="createProf" class="space-y-5">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">{{ newError }}</div>

          <!-- Datos básicos -->
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
              <Input v-model="newForm.dni" placeholder="DNI" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Matrícula <span class="text-destructive">*</span></label>
              <Input v-model="newForm.matricula" placeholder="Matrícula profesional" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Teléfono <span class="text-destructive">*</span></label>
              <Input v-model="newForm.telefono" placeholder="Teléfono" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Email <span class="text-destructive">*</span></label>
              <Input v-model="newForm.email" type="email" placeholder="correo@ejemplo.com" required />
            </div>
          </div>

          <!-- Sucursales -->
          <div class="space-y-2">
            <label class="text-sm font-medium">Sucursales donde atiende <span class="text-destructive">*</span></label>
            <div class="flex flex-wrap gap-2">
              <button v-for="s in sucursales" :key="s.id" type="button" @click="toggle(newForm.sucursalesIds, s.id)"
                :class="['inline-flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-full border transition-colors font-medium',
                  newForm.sucursalesIds.includes(s.id) ? 'bg-primary text-primary-foreground border-primary' : 'hover:bg-muted border-input']">
                <Check v-if="newForm.sucursalesIds.includes(s.id)" class="h-3 w-3" />
                {{ s.nombre }}
              </button>
            </div>
          </div>

          <!-- Especialidades -->
          <div class="space-y-2">
            <label class="text-sm font-medium">Especialidades</label>
            <div class="flex flex-wrap gap-2">
              <button v-for="e in especialidades" :key="e.id" type="button" @click="toggle(newForm.especialidadesIds, e.id)"
                :class="['inline-flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-full border transition-colors',
                  newForm.especialidadesIds.includes(e.id) ? 'bg-secondary text-secondary-foreground border-secondary' : 'hover:bg-muted border-input']">
                <Check v-if="newForm.especialidadesIds.includes(e.id)" class="h-3 w-3" />
                {{ e.nombre }}
              </button>
            </div>
          </div>

          <!-- Obras Sociales -->
          <div class="space-y-2">
            <div class="flex items-center justify-between">
              <label class="text-sm font-medium">Obras Sociales que acepta</label>
              <div class="flex gap-2">
                <button type="button" @click="toggleAll(newForm.obrasSocialesIds, obrasSociales, true)"
                  class="text-xs px-2 py-1 rounded border hover:bg-muted transition-colors">Seleccionar todo</button>
                <button type="button" @click="toggleAll(newForm.obrasSocialesIds, obrasSociales, false)"
                  class="text-xs px-2 py-1 rounded border hover:bg-muted transition-colors">Limpiar</button>
              </div>
            </div>
            <div class="flex flex-wrap gap-1.5 max-h-32 overflow-y-auto border rounded-md p-2">
              <button v-for="os in obrasSociales" :key="os.id" type="button" @click="toggle(newForm.obrasSocialesIds, os.id)"
                :class="['inline-flex items-center gap-1 text-xs px-2 py-1 rounded border transition-colors',
                  newForm.obrasSocialesIds.includes(os.id) ? 'bg-accent text-accent-foreground border-accent' : 'hover:bg-muted border-input']">
                <Check v-if="newForm.obrasSocialesIds.includes(os.id)" class="h-2.5 w-2.5" />
                {{ os.nombre }}
              </button>
            </div>
            <p class="text-xs text-muted-foreground">{{ newForm.obrasSocialesIds.length }} seleccionada(s)</p>
          </div>

          <!-- Estudios que realiza -->
          <div class="space-y-2">
            <div class="flex items-center justify-between">
              <label class="text-sm font-medium">Estudios que realiza</label>
              <div class="flex gap-2">
                <button type="button" @click="toggleAll(newForm.estudiosIds, estudios, true)"
                  class="text-xs px-2 py-1 rounded border hover:bg-muted transition-colors">Seleccionar todo</button>
                <button type="button" @click="toggleAll(newForm.estudiosIds, estudios, false)"
                  class="text-xs px-2 py-1 rounded border hover:bg-muted transition-colors">Limpiar</button>
              </div>
            </div>
            <div class="flex flex-wrap gap-1.5 max-h-40 overflow-y-auto border rounded-md p-2">
              <button v-for="e in estudios" :key="e.id" type="button" @click="toggle(newForm.estudiosIds, e.id)"
                :class="['inline-flex items-center gap-1 text-xs px-2 py-1 rounded border transition-colors',
                  newForm.estudiosIds.includes(e.id) ? 'bg-primary/10 text-primary border-primary/30' : 'hover:bg-muted border-input']">
                <Check v-if="newForm.estudiosIds.includes(e.id)" class="h-2.5 w-2.5" />
                {{ e.nombre }}
              </button>
            </div>
            <p class="text-xs text-muted-foreground">{{ newForm.estudiosIds.length }} seleccionado(s) — estudios NO seleccionados se entiende que no los realiza</p>
          </div>

          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Registrar Profesional" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Table -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando...</div>
        <div v-else-if="profesionales.length === 0" class="p-6 text-center text-muted-foreground">
          <Stethoscope class="h-8 w-8 mx-auto mb-2 opacity-50" />
          <p class="text-sm">No se encontraron profesionales</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead class="border-b bg-muted/50">
              <tr>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Profesional</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Matrícula</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Especialidades</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Sucursales</th>
                <th class="px-4 py-3 text-left font-medium text-muted-foreground">Estudios</th>
                <th class="px-4 py-3 text-right font-medium text-muted-foreground">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="p in profesionales" :key="p.id">
                <!-- Normal row -->
                <tr v-if="editingId !== p.id" class="border-b hover:bg-muted/30 transition-colors">
                  <td class="px-4 py-3">
                    <div class="font-medium">{{ p.apellido ? p.apellido + ', ' + p.nombre : p.nombre }}</div>
                    <div class="text-xs text-muted-foreground">{{ p.telefono ?? "" }}{{ p.email ? ` · ${p.email}` : "" }}</div>
                  </td>
                  <td class="px-4 py-3 text-muted-foreground font-mono text-xs">{{ p.matricula ?? "—" }}</td>
                  <td class="px-4 py-3">
                    <div class="flex flex-wrap gap-1">
                      <span v-for="n in name(especialidades, p.especialidadesIds)" :key="n"
                        class="text-xs px-2 py-0.5 rounded-full bg-secondary/20 text-secondary-foreground font-medium">{{ n }}</span>
                      <span v-if="p.especialidadesIds.length === 0" class="text-xs text-muted-foreground">—</span>
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <div class="flex flex-wrap gap-1">
                      <span v-for="n in name(sucursales, p.sucursalesIds)" :key="n"
                        class="text-xs px-1.5 py-0.5 rounded bg-primary/10 text-primary">{{ n }}</span>
                    </div>
                  </td>
                  <td class="px-4 py-3">
                    <span class="text-xs text-muted-foreground">{{ p.estudiosIds.length }} estudio{{ p.estudiosIds.length !== 1 ? 's' : '' }}</span>
                  </td>
                  <td class="px-4 py-3 text-right">
                    <div v-if="confirmDeleteId === p.id" class="flex items-center justify-end gap-2">
                      <span class="text-xs text-muted-foreground">¿Dar de baja?</span>
                      <button @click="deleteProf(p.id)" :disabled="deletingId === p.id" class="text-xs px-2 py-1 rounded bg-destructive text-destructive-foreground hover:bg-destructive/90">Sí</button>
                      <button @click="confirmDeleteId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">No</button>
                    </div>
                    <div v-else class="flex items-center justify-end gap-2">
                      <button v-if="isAdminOrRecepcion" @click="startEdit(p)" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Editar</button>
                      <button v-if="isAdmin" @click="confirmDeleteId = p.id" class="text-xs px-3 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Baja</button>
                    </div>
                  </td>
                </tr>

                <!-- Edit row -->
                <tr v-else class="border-b bg-muted/10">
                  <td colspan="6" class="px-4 py-4">
                    <div class="space-y-4">
                      <div v-if="editError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-2 text-xs text-destructive">{{ editError }}</div>

                      <!-- Datos básicos -->
                      <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
                        <Input v-model="editForm.apellido" placeholder="Apellido" class="h-8 text-sm" />
                        <Input v-model="editForm.nombre" placeholder="Nombre" class="h-8 text-sm" />
                        <Input v-model="editForm.matricula" placeholder="Matrícula" class="h-8 text-sm font-mono" />
                        <Input v-model="editForm.telefono" placeholder="Teléfono" class="h-8 text-sm" />
                        <Input v-model="editForm.email" placeholder="Email" class="h-8 text-sm lg:col-span-2" />
                      </div>

                      <!-- Sucursales -->
                      <div class="space-y-1.5">
                        <label class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Sucursales</label>
                        <div class="flex flex-wrap gap-2">
                          <button v-for="s in sucursales" :key="s.id" type="button" @click="toggle(editForm.sucursalesIds, s.id)"
                            :class="['inline-flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full border transition-colors',
                              editForm.sucursalesIds.includes(s.id) ? 'bg-primary text-primary-foreground border-primary' : 'hover:bg-muted border-input']">
                            <Check v-if="editForm.sucursalesIds.includes(s.id)" class="h-3 w-3" />
                            {{ s.nombre }}
                          </button>
                        </div>
                      </div>

                      <!-- Especialidades -->
                      <div class="space-y-1.5">
                        <label class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Especialidades</label>
                        <div class="flex flex-wrap gap-2">
                          <button v-for="e in especialidades" :key="e.id" type="button" @click="toggle(editForm.especialidadesIds, e.id)"
                            :class="['inline-flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full border transition-colors',
                              editForm.especialidadesIds.includes(e.id) ? 'bg-secondary text-secondary-foreground border-secondary' : 'hover:bg-muted border-input']">
                            <Check v-if="editForm.especialidadesIds.includes(e.id)" class="h-3 w-3" />
                            {{ e.nombre }}
                          </button>
                        </div>
                      </div>

                      <!-- Estudios que realiza -->
                      <div class="space-y-1.5">
                        <div class="flex items-center justify-between">
                          <label class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Estudios que realiza
                            <span class="ml-2 text-primary normal-case">{{ editForm.estudiosIds.length }} seleccionados</span>
                          </label>
                          <div class="flex gap-1.5">
                            <button type="button" @click="toggleAll(editForm.estudiosIds, estudios, true)"
                              class="text-xs text-primary hover:underline">Seleccionar todo</button>
                            <span class="text-muted-foreground">·</span>
                            <button type="button" @click="toggleAll(editForm.estudiosIds, estudios, false)"
                              class="text-xs text-primary hover:underline">Limpiar</button>
                            <span class="text-muted-foreground">·</span>
                            <button type="button" @click="showEstudios = !showEstudios" class="text-xs text-primary hover:underline">
                              {{ showEstudios ? 'Ocultar' : 'Ver todos' }}
                            </button>
                          </div>
                        </div>
                        <div v-if="showEstudios" class="flex flex-wrap gap-1.5 max-h-48 overflow-y-auto border rounded-md p-2 bg-muted/20">
                          <button v-for="e in estudios" :key="e.id" type="button" @click="toggle(editForm.estudiosIds, e.id)"
                            :class="['inline-flex items-center gap-1 text-xs px-2 py-1 rounded border transition-colors',
                              editForm.estudiosIds.includes(e.id) ? 'bg-primary/15 text-primary border-primary/40 font-medium' : 'hover:bg-muted border-input text-muted-foreground']">
                            <Check v-if="editForm.estudiosIds.includes(e.id)" class="h-2.5 w-2.5" />
                            {{ e.nombre }}
                          </button>
                        </div>
                        <div v-else class="flex flex-wrap gap-1">
                          <span v-for="n in name(estudios, editForm.estudiosIds)" :key="n"
                            class="text-xs px-1.5 py-0.5 rounded bg-primary/10 text-primary">{{ n }}</span>
                        </div>
                      </div>

                      <!-- Obras Sociales -->
                      <div class="space-y-1.5">
                        <div class="flex items-center justify-between">
                          <label class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Obras Sociales
                            <span class="ml-2 text-secondary normal-case">{{ editForm.obrasSocialesIds.length }} seleccionadas</span>
                          </label>
                          <div class="flex gap-1.5">
                            <button type="button" @click="toggleAll(editForm.obrasSocialesIds, obrasSociales, true)"
                              class="text-xs px-2 py-0.5 rounded border hover:bg-muted transition-colors">Seleccionar todo</button>
                            <button type="button" @click="toggleAll(editForm.obrasSocialesIds, obrasSociales, false)"
                              class="text-xs px-2 py-0.5 rounded border hover:bg-muted transition-colors">Limpiar</button>
                          </div>
                        </div>
                        <div class="flex flex-wrap gap-1.5 max-h-32 overflow-y-auto border rounded-md p-2 bg-muted/20">
                          <button v-for="os in obrasSociales" :key="os.id" type="button" @click="toggle(editForm.obrasSocialesIds, os.id)"
                            :class="['inline-flex items-center gap-1 text-xs px-2 py-1 rounded border transition-colors',
                              editForm.obrasSocialesIds.includes(os.id) ? 'bg-secondary/20 text-secondary-foreground border-secondary/40 font-medium' : 'hover:bg-muted border-input text-muted-foreground']">
                            <Check v-if="editForm.obrasSocialesIds.includes(os.id)" class="h-2.5 w-2.5" />
                            {{ os.nombre }}
                          </button>
                        </div>
                      </div>

                      <div class="flex justify-end gap-2 pt-1">
                        <button @click="saveEdit(p)" :disabled="editSaving" class="text-xs px-4 py-1.5 rounded bg-primary text-primary-foreground hover:bg-primary/90 transition-colors font-medium">
                          {{ editSaving ? "Guardando..." : "Guardar cambios" }}
                        </button>
                        <button @click="editingId = null" class="text-xs px-4 py-1.5 rounded border hover:bg-muted transition-colors">Cancelar</button>
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
