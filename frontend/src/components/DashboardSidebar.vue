<script setup lang="ts">
import { RouterLink, useRoute, useRouter } from "vue-router";
import { LogOut, Activity, PanelLeft } from "lucide-vue-next";
import { useAuthStore } from "@/stores/auth";
import { sidebarConfig, roleLabels, type UserRole } from "@/types/roles";
import Button from "@/components/ui/Button.vue";
import Avatar from "@/components/ui/Avatar.vue";
import AvatarFallback from "@/components/ui/AvatarFallback.vue";
import { computed } from "vue";

const props = defineProps<{ collapsed: boolean }>();
const emit = defineEmits<{ "update:collapsed": [value: boolean] }>();

const auth = useAuthStore();
const route = useRoute();
const router = useRouter();

function handleLogout() {
  auth.logout();
  router.push("/login");
}

const sections = computed(() =>
  auth.primaryRole ? sidebarConfig[auth.primaryRole as UserRole] ?? [] : []
);
const initials = computed(() => auth.username.slice(0, 2).toUpperCase());
</script>

<template>
  <aside
    v-if="auth.isAuthenticated"
    :class="[
      'bg-sidebar border-r border-sidebar-border flex flex-col h-screen sticky top-0 transition-all duration-300 z-40',
      collapsed ? 'w-0 overflow-hidden' : 'w-64'
    ]"
  >
    <!-- Header -->
    <div class="p-4">
      <RouterLink to="/dashboard" class="flex items-center gap-2">
        <Activity class="h-6 w-6 text-primary" />
        <span class="text-lg font-bold text-primary">ECOMED</span>
      </RouterLink>
    </div>
    <hr class="border-sidebar-border" />

    <!-- Content -->
    <nav class="flex-1 overflow-y-auto px-2 py-2">
      <div v-for="section in sections" :key="section.title" class="mb-4">
        <p class="px-3 py-1.5 text-xs font-medium text-sidebar-foreground/70 uppercase tracking-wider">
          {{ section.title }}
        </p>
        <ul class="space-y-0.5">
          <li v-for="item in section.items" :key="item.path">
            <RouterLink
              :to="item.path"
              :class="[
                'flex items-center gap-2 px-3 py-2 text-sm rounded-md transition-colors',
                route.path === item.path
                  ? 'bg-sidebar-accent text-sidebar-accent-foreground font-medium'
                  : 'text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground'
              ]"
            >
              <component :is="item.icon" class="h-4 w-4" />
              <span>{{ item.label }}</span>
            </RouterLink>
          </li>
        </ul>
      </div>
    </nav>

    <!-- Footer -->
    <div class="p-3 border-t border-sidebar-border">
      <div class="flex items-center gap-2 px-1 mb-2">
        <Avatar class="h-8 w-8">
          <AvatarFallback class="bg-primary text-primary-foreground text-xs">{{ initials }}</AvatarFallback>
        </Avatar>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium truncate">{{ auth.username }}</p>
          <p class="text-xs text-muted-foreground">{{ roleLabels[auth.primaryRole as UserRole] ?? auth.primaryRole }}</p>
        </div>
      </div>
      <Button variant="ghost" size="sm" class="w-full justify-start" @click="handleLogout()">
        <LogOut class="h-4 w-4 mr-2" /> Cerrar sesión
      </Button>
    </div>
  </aside>
</template>
