<script setup lang="ts">
import { useAuthStore } from "@/stores/auth";
import AdminDashboard from "./AdminDashboard.vue";
import PatientDashboard from "./PatientDashboard.vue";
import DoctorDashboard from "./DoctorDashboard.vue";
import AdminStaffDashboard from "./AdminStaffDashboard.vue";
import AccountingDashboard from "./AccountingDashboard.vue";

const auth = useAuthStore();
</script>

<template>
  <AdminDashboard       v-if="auth.primaryRole === 'ROLE_ADMIN'" />
  <DoctorDashboard      v-else-if="auth.primaryRole === 'ROLE_DOCTOR'" />
  <DoctorDashboard      v-else-if="auth.primaryRole === 'ROLE_DERIVADOR'" />
  <AdminStaffDashboard  v-else-if="auth.primaryRole === 'ROLE_RECEPCION'" />
  <AccountingDashboard  v-else-if="auth.primaryRole === 'ROLE_FACTURACION'" />
  <PatientDashboard     v-else-if="auth.primaryRole === 'ROLE_PACIENTE'" />
  <!-- Fallback genérico -->
  <div v-else class="p-8 text-center text-muted-foreground">
    <p class="text-lg font-medium">Bienvenido, {{ auth.username }}</p>
    <p class="text-sm mt-1">Usá el menú lateral para navegar.</p>
  </div>
</template>
