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
import Select from "@/components/ui/Select.vue";

interface UserDto {
  id: number;
  username: string;
  email: string | null;
  isActive: boolean;
  createdAt: string;
  roles: string[];
}

const auth = useAuthStore();
const API = "http://localhost:8080/api/users";
const headers = () => ({ Authorization: `Bearer ${auth.token}` });

const users = ref<UserDto[]>([]);
const loading = ref(true);
const globalError = ref("");

// --- Nuevo usuario ---
const showNewForm = ref(false);
const newForm = ref({ username: "", password: "", email: "", role: "ROLE_PACIENTE" });
const newSaving = ref(false);
const newError = ref("");

// --- Edición inline ---
const editingId = ref<number | null>(null);
const editForm = ref({ username: "", email: "", password: "", role: "" });
const editSaving = ref(false);
const editError = ref("");

// --- Eliminación ---
const deletingId = ref<number | null>(null);
const confirmDeleteId = ref<number | null>(null);

const roleOptions = [
  { value: "ROLE_ADMIN",       label: "Administrador" },
  { value: "ROLE_DOCTOR",      label: "Doctor" },
  { value: "ROLE_RECEPCION",   label: "Recepción" },
  { value: "ROLE_FACTURACION", label: "Facturación" },
  { value: "ROLE_PACIENTE",    label: "Paciente" },
];
const roleLabel: Record<string, string> = Object.fromEntries(roleOptions.map(o => [o.value, o.label]));

async function loadUsers() {
  loading.value = true;
  globalError.value = "";
  try {
    const res = await axios.get(API, { headers: headers() });
    users.value = res.data;
  } catch {
    globalError.value = "No se pudieron cargar los usuarios.";
  } finally {
    loading.value = false;
  }
}

onMounted(loadUsers);

// --- Crear ---
async function createUser() {
  newError.value = "";
  if (!newForm.value.username || !newForm.value.password) {
    newError.value = "Usuario y contraseña son obligatorios.";
    return;
  }
  newSaving.value = true;
  try {
    await axios.post("http://localhost:8080/api/users/register", {
      username: newForm.value.username,
      password: newForm.value.password,
      email: newForm.value.email || null,
      roles: [newForm.value.role],
    }, { headers: headers() });
    newForm.value = { username: "", password: "", email: "", role: "ROLE_PACIENTE" };
    showNewForm.value = false;
    await loadUsers();
  } catch (e: any) {
    newError.value = e?.response?.data?.error ?? "Error al crear el usuario.";
  } finally {
    newSaving.value = false;
  }
}

// --- Editar ---
function startEdit(u: UserDto) {
  editingId.value = u.id;
  editForm.value = { username: u.username, email: u.email ?? "", password: "", role: u.roles[0] ?? "ROLE_PACIENTE" };
  editError.value = "";
  confirmDeleteId.value = null;
}

function cancelEdit() {
  editingId.value = null;
  editError.value = "";
}

async function saveEdit(u: UserDto) {
  editError.value = "";
  editSaving.value = true;
  try {
    // Actualizar datos del usuario
    const res = await axios.put(`${API}/${u.id}`, {
      username: editForm.value.username,
      email: editForm.value.email || null,
      password: editForm.value.password || null,
    }, { headers: headers() });

    // Si cambió el rol, actualizarlo también
    if (editForm.value.role !== u.roles[0]) {
      await axios.put(`${API}/${u.id}/role`, { role: editForm.value.role }, { headers: headers() });
    }

    const idx = users.value.findIndex(x => x.id === u.id);
    if (idx !== -1) {
      users.value[idx] = { ...res.data, roles: [editForm.value.role] };
    }
    editingId.value = null;
  } catch (e: any) {
    editError.value = e?.response?.data?.error ?? "Error al guardar cambios.";
  } finally {
    editSaving.value = false;
  }
}

// --- Eliminar ---
async function deleteUser(id: number) {
  deletingId.value = id;
  try {
    await axios.delete(`${API}/${id}`, { headers: headers() });
    users.value = users.value.filter(u => u.id !== id);
    confirmDeleteId.value = null;
  } catch (e: any) {
    globalError.value = e?.response?.data?.error ?? "Error al eliminar el usuario.";
    setTimeout(() => { globalError.value = ""; }, 3000);
  } finally {
    deletingId.value = null;
  }
}

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString("es-AR", { day: "2-digit", month: "2-digit", year: "numeric" });
}
</script>

<template>
  <div class="space-y-6">

    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 class="text-2xl font-bold">Usuarios del Sistema</h1>
        <p class="text-muted-foreground text-sm">Administrá los usuarios y sus roles</p>
      </div>
      <button
        @click="showNewForm = !showNewForm"
        class="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground hover:bg-primary/90 transition-colors"
      >
        {{ showNewForm ? "✕ Cancelar" : "+ Nuevo Usuario" }}
      </button>
    </div>

    <!-- Error global -->
    <div v-if="globalError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
      {{ globalError }}
    </div>

    <!-- Formulario nuevo usuario -->
    <Card v-if="showNewForm">
      <CardHeader>
        <CardTitle class="text-base">Registrar nuevo usuario</CardTitle>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="createUser" class="space-y-4">
          <div v-if="newError" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
            {{ newError }}
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-sm font-medium">Usuario <span class="text-destructive">*</span></label>
              <Input v-model="newForm.username" placeholder="nombre_usuario" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Contraseña <span class="text-destructive">*</span></label>
              <Input v-model="newForm.password" type="password" placeholder="••••••••" required />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Email <span class="text-muted-foreground text-xs">(opcional)</span></label>
              <Input v-model="newForm.email" type="email" placeholder="correo@ejemplo.com" />
            </div>
            <div class="space-y-1">
              <label class="text-sm font-medium">Rol <span class="text-destructive">*</span></label>
              <Select v-model="newForm.role" :options="roleOptions" />
            </div>
          </div>
          <div class="flex justify-end gap-2">
            <Button type="button" variant="outline" @click="showNewForm = false">Cancelar</Button>
            <Button type="submit" :disabled="newSaving">{{ newSaving ? "Guardando..." : "Crear Usuario" }}</Button>
          </div>
        </form>
      </CardContent>
    </Card>

    <!-- Tabla -->
    <Card>
      <CardContent class="p-0">
        <div v-if="loading" class="p-6 text-sm text-muted-foreground">Cargando...</div>
        <table v-else class="w-full text-sm">
          <thead class="border-b bg-muted/50">
            <tr>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">#</th>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">Usuario</th>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">Email</th>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">Rol</th>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">Estado</th>
              <th class="px-4 py-3 text-left font-medium text-muted-foreground">Alta</th>
              <th class="px-4 py-3 text-right font-medium text-muted-foreground">Acciones</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="u in users" :key="u.id">
              <!-- Fila normal -->
              <tr v-if="editingId !== u.id" class="border-b hover:bg-muted/30 transition-colors">
                <td class="px-4 py-3 text-muted-foreground text-xs">{{ u.id }}</td>
                <td class="px-4 py-3 font-medium">{{ u.username }}</td>
                <td class="px-4 py-3 text-muted-foreground">{{ u.email ?? "—" }}</td>
                <td class="px-4 py-3">
                  <span class="text-xs px-2 py-0.5 rounded-full bg-primary/10 text-primary font-medium">
                    {{ roleLabel[u.roles[0]] ?? u.roles[0] }}
                  </span>
                </td>
                <td class="px-4 py-3">
                  <span :class="['text-xs px-2 py-0.5 rounded-full font-medium', u.isActive ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700']">
                    {{ u.isActive ? "Activo" : "Inactivo" }}
                  </span>
                </td>
                <td class="px-4 py-3 text-muted-foreground">{{ formatDate(u.createdAt) }}</td>
                <td class="px-4 py-3 text-right">
                  <div v-if="confirmDeleteId === u.id" class="flex items-center justify-end gap-2">
                    <span class="text-xs text-muted-foreground">¿Eliminar?</span>
                    <button
                      @click="deleteUser(u.id)"
                      :disabled="deletingId === u.id"
                      class="text-xs px-2 py-1 rounded bg-destructive text-destructive-foreground hover:bg-destructive/90"
                    >Sí</button>
                    <button @click="confirmDeleteId = null" class="text-xs px-2 py-1 rounded border hover:bg-muted">No</button>
                  </div>
                  <div v-else class="flex items-center justify-end gap-2">
                    <button @click="startEdit(u)" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Editar</button>
                    <button @click="confirmDeleteId = u.id" class="text-xs px-3 py-1 rounded border border-destructive/40 text-destructive hover:bg-destructive/10 transition-colors">Eliminar</button>
                  </div>
                </td>
              </tr>

              <!-- Fila de edición -->
              <tr v-else class="border-b bg-muted/20">
                <td class="px-4 py-3 text-muted-foreground text-xs">{{ u.id }}</td>
                <td class="px-4 py-2">
                  <Input v-model="editForm.username" class="h-8 text-sm" />
                </td>
                <td class="px-4 py-2">
                  <Input v-model="editForm.email" type="email" placeholder="Email" class="h-8 text-sm" />
                </td>
                <td class="px-4 py-2">
                  <Select v-model="editForm.role" :options="roleOptions" class="h-8 text-xs" />
                </td>
                <td class="px-4 py-2">
                  <Input v-model="editForm.password" type="password" placeholder="Nueva contraseña" class="h-8 text-sm" />
                </td>
                <td class="px-4 py-3 text-muted-foreground">{{ formatDate(u.createdAt) }}</td>
                <td class="px-4 py-2 text-right">
                  <div class="flex items-center justify-end gap-2">
                    <div v-if="editError" class="text-xs text-destructive mr-2">{{ editError }}</div>
                    <button
                      @click="saveEdit(u)"
                      :disabled="editSaving"
                      class="text-xs px-3 py-1 rounded bg-primary text-primary-foreground hover:bg-primary/90 transition-colors"
                    >{{ editSaving ? "..." : "Guardar" }}</button>
                    <button @click="cancelEdit()" class="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors">Cancelar</button>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </CardContent>
    </Card>

  </div>
</template>
