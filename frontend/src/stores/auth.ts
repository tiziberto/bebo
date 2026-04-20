import { defineStore } from "pinia";
import { ref, computed } from "vue";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

interface JwtPayload {
  sub: string;
  roles?: string[];
  iat?: number;
  exp?: number;
}

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(localStorage.getItem("token"));
  const loading = ref(false);

  // Decodifica el token para obtener datos del usuario
  const payload = computed<JwtPayload | null>(() => {
    if (!token.value) return null;
    try {
      return jwtDecode<JwtPayload>(token.value);
    } catch {
      return null;
    }
  });

  const isAuthenticated = computed(() => !!token.value && !!payload.value);
  const username = computed(() => payload.value?.sub ?? "");
  const roles = computed<string[]>(() => payload.value?.roles ?? []);
  const primaryRole = computed(() => roles.value[0] ?? null);

  function hasAnyRole(requiredRoles: string[]): boolean {
    return requiredRoles.some((r) => roles.value.includes(r));
  }

  async function login(username: string, password: string) {
    loading.value = true;
    try {
      const res = await axios.post("http://localhost:8080/api/users/login", {
        username,
        password,
      });
      token.value = res.data.token;
      localStorage.setItem("token", res.data.token);
    } finally {
      loading.value = false;
    }
  }

  function logout() {
    token.value = null;
    localStorage.removeItem("token");
  }

  function init() {
    const stored = localStorage.getItem("token");
    if (!stored) return;
    try {
      const decoded = jwtDecode<JwtPayload>(stored);
      const now = Math.floor(Date.now() / 1000);
      if (decoded.exp && decoded.exp < now) {
        logout();
      } else {
        token.value = stored;
      }
    } catch {
      logout();
    }
  }

  return {
    token,
    loading,
    isAuthenticated,
    username,
    roles,
    primaryRole,
    hasAnyRole,
    login,
    logout,
    init,
  };
});
