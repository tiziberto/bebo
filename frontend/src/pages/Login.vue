<script setup lang="ts">
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import Button from "@/components/ui/Button.vue";
import Input from "@/components/ui/Input.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import CardDescription from "@/components/ui/CardDescription.vue";
import { Activity, ArrowLeft, KeyRound, Loader2 } from "lucide-vue-next";

const username = ref("");
const password = ref("");
const showRecovery = ref(false);
const recoveryEmail = ref("");
const loading = ref(false);
const errorMsg = ref("");

const auth = useAuthStore();
const router = useRouter();

const ROLE_REDIRECT: Record<string, string> = {
  ROLE_ADMIN:       "/dashboard/admin",
  ROLE_DOCTOR:      "/dashboard/doctor",
  ROLE_RECEPCION:   "/dashboard/reception",
  ROLE_FACTURACION: "/dashboard/billing",
  ROLE_PACIENTE:    "/dashboard/patient",
};

const roleLabels: Record<string, string> = {
  ROLE_ADMIN:       "Administrador",
  ROLE_DOCTOR:      "Doctor",
  ROLE_RECEPCION:   "Recepción",
  ROLE_FACTURACION: "Facturación",
  ROLE_PACIENTE:    "Paciente",
};

const mockUsers = [
  { username: "ADMIN", role: "ROLE_ADMIN" },
  { username: "DOC",   role: "ROLE_DOCTOR" },
  { username: "REC",   role: "ROLE_RECEPCION" },
  { username: "FAC",   role: "ROLE_FACTURACION" },
  { username: "PAC",   role: "ROLE_PACIENTE" },
];

async function handleLogin() {
  errorMsg.value = "";
  loading.value = true;
  try {
    await auth.login(username.value, password.value);
    const role = auth.primaryRole;
    router.push(ROLE_REDIRECT[role ?? ""] ?? "/dashboard");
  } catch (e: any) {
    errorMsg.value =
      e?.response?.data?.message ??
      e?.response?.data ??
      "Usuario o contraseña incorrectos";
  } finally {
    loading.value = false;
  }
}

function handleRecovery() {
  showRecovery.value = false;
}

function fillCredentials(u: string) {
  username.value = u;
  password.value = "1234";
}
</script>

<template>
  <!-- Recovery -->
  <div v-if="showRecovery" class="min-h-screen flex items-center justify-center bg-muted/30 px-4">
    <Card class="w-full max-w-md">
      <CardHeader class="text-center">
        <div class="flex justify-center mb-3">
          <KeyRound class="h-10 w-10 text-primary" />
        </div>
        <CardTitle>Recuperar Contraseña</CardTitle>
        <CardDescription>Ingresá tu email y te enviaremos instrucciones</CardDescription>
      </CardHeader>
      <CardContent>
        <form @submit.prevent="handleRecovery" class="space-y-4">
          <div class="space-y-2">
            <label class="text-sm font-medium">Email</label>
            <Input v-model="recoveryEmail" type="email" placeholder="tu@email.com" required />
          </div>
          <Button type="submit" class="w-full">Enviar instrucciones</Button>
          <Button type="button" variant="ghost" class="w-full" @click="showRecovery = false">
            <ArrowLeft class="h-4 w-4 mr-1" /> Volver al login
          </Button>
        </form>
      </CardContent>
    </Card>
  </div>

  <!-- Login -->
  <div v-else class="min-h-screen flex items-center justify-center bg-muted/30 px-4">
    <div class="w-full max-w-md space-y-6">
      <div class="text-center">
        <RouterLink to="/" class="inline-flex items-center gap-2 mb-6">
          <Activity class="h-8 w-8 text-primary" />
          <span class="text-2xl font-bold text-primary">ECOMED</span>
        </RouterLink>
        <h1 class="text-2xl font-bold">Iniciar Sesión</h1>
        <p class="text-muted-foreground text-sm mt-1">Ingresá tus credenciales para acceder al sistema</p>
      </div>

      <Card>
        <CardContent class="pt-6">
          <form @submit.prevent="handleLogin" class="space-y-4">

            <!-- Error -->
            <div v-if="errorMsg" class="rounded-md bg-destructive/10 border border-destructive/30 px-4 py-3 text-sm text-destructive">
              {{ errorMsg }}
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Usuario</label>
              <Input
                v-model="username"
                type="text"
                placeholder="Tu nombre de usuario"
                autocomplete="username"
                required
              />
            </div>

            <div class="space-y-2">
              <label class="text-sm font-medium">Contraseña</label>
              <Input
                v-model="password"
                type="password"
                placeholder="••••••••"
                autocomplete="current-password"
                required
              />
            </div>

            <Button type="submit" class="w-full" :disabled="loading">
              <Loader2 v-if="loading" class="h-4 w-4 mr-2 animate-spin" />
              {{ loading ? "Ingresando..." : "Ingresar" }}
            </Button>

            <button
              type="button"
              class="w-full text-sm text-primary hover:underline"
              @click="showRecovery = true"
            >
              ¿Olvidaste tu contraseña?
            </button>
          </form>
        </CardContent>
      </Card>

      <!-- Demo credentials -->
      <Card class="border-dashed">
        <CardHeader class="pb-2">
          <CardTitle class="text-sm">Usuarios de prueba</CardTitle>
          <CardDescription class="text-xs">Contraseña para todos: 1234</CardDescription>
        </CardHeader>
        <CardContent class="pt-0">
          <div class="space-y-1">
            <button
              v-for="u in mockUsers"
              :key="u.username"
              type="button"
              class="flex items-center justify-between w-full px-2 py-1.5 text-xs rounded hover:bg-muted transition-colors"
              @click="fillCredentials(u.username)"
            >
              <span class="text-muted-foreground font-mono">{{ u.username }}</span>
              <span class="font-medium text-primary">{{ roleLabels[u.role] }}</span>
            </button>
          </div>
        </CardContent>
      </Card>

      <div class="text-center">
        <RouterLink to="/" class="text-sm text-muted-foreground hover:text-primary">
          <ArrowLeft class="h-3 w-3 inline mr-1" /> Volver al sitio
        </RouterLink>
      </div>
    </div>
  </div>
</template>
