import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      component: () => import("@/components/PublicLayout.vue"),
      children: [
        { path: "", component: () => import("@/pages/Index.vue") },
        { path: "servicios", component: () => import("@/pages/Servicios.vue") },
        { path: "reservar", component: () => import("@/pages/ReservarTurno.vue") },
        { path: "portal", component: () => import("@/pages/PortalPaciente.vue") },
        { path: "contacto", component: () => import("@/pages/Contacto.vue") },
      ],
    },
    { path: "/login", component: () => import("@/pages/Login.vue") },
    { path: "/acceso-denegado", component: () => import("@/pages/AccessDenied.vue") },
    {
      path: "/dashboard",
      component: () => import("@/components/DashboardLayout.vue"),
      meta: { requiresAuth: true },
      children: [
        { path: "", component: () => import("@/pages/dashboard/DashboardHome.vue") },
        { path: "admin", component: () => import("@/pages/dashboard/AdminDashboard.vue") },
        { path: "usuarios", component: () => import("@/pages/dashboard/UsersPage.vue") },
        { path: "doctor", component: () => import("@/pages/dashboard/DoctorDashboard.vue") },
        { path: "reception", component: () => import("@/pages/dashboard/AdminStaffDashboard.vue") },
        { path: "billing", component: () => import("@/pages/dashboard/AccountingDashboard.vue") },
        { path: "patient", component: () => import("@/pages/dashboard/PatientDashboard.vue") },
        // Módulo 2: Gestión de Pacientes y Turnos
        { path: "pacientes", component: () => import("@/pages/dashboard/PacientesPage.vue") },
        { path: "alta-pacientes", component: () => import("@/pages/dashboard/PacientesPage.vue") },
        { path: "profesionales", component: () => import("@/pages/dashboard/ProfesionalesPage.vue") },
        { path: "obras-sociales", component: () => import("@/pages/dashboard/ObrasSocialesPage.vue") },
        { path: "estudios", component: () => import("@/pages/dashboard/EstudiosPage.vue") },
        { path: "turnos", component: () => import("@/pages/dashboard/TurnosPage.vue") },
        { path: "gestion-turnos", component: () => import("@/pages/dashboard/TurnosPage.vue") },
        { path: "agendas", component: () => import("@/pages/dashboard/AgendaPage.vue") },
        { path: "mi-agenda", component: () => import("@/pages/dashboard/AgendaPage.vue") },
        // ROLE_DERIVADOR routes (placeholders — full implementation in Studies module)
        { path: "solicitar-estudio", component: () => import("@/pages/dashboard/DashboardPlaceholder.vue") },
        { path: "estudios-solicitados", component: () => import("@/pages/dashboard/DashboardPlaceholder.vue") },
        { path: "estado-pedidos", component: () => import("@/pages/dashboard/DashboardPlaceholder.vue") },
        { path: "mis-pacientes-derivados", component: () => import("@/pages/dashboard/DashboardPlaceholder.vue") },
        { path: ":section(.*)", component: () => import("@/pages/dashboard/DashboardPlaceholder.vue") },
      ],
    },
    { path: "/:pathMatch(.*)*", component: () => import("@/pages/NotFound.vue") },
  ],
});

// Navigation guard for protected routes
router.beforeEach((to, from, next) => {
  if (to.matched.some((r) => r.meta.requiresAuth)) {
    const stored = localStorage.getItem("token");
    if (!stored) {
      next("/login");
      return;
    }
  }
  next();
});

export default router;
