<script setup lang="ts">
import { ref, reactive } from "vue";
import { format } from "date-fns";
import { es } from "date-fns/locale";
import { CheckCircle2 } from "lucide-vue-next";
import Button from "@/components/ui/Button.vue";
import Input from "@/components/ui/Input.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import Select from "@/components/ui/Select.vue";

const estudios = [
  "Ecografía", "Radiología Digital", "Tomografía Computada",
  "Resonancia Magnética", "Mamografía Digital", "Densitometría Ósea",
];

const obrasSociales = ["OSDE", "Swiss Medical", "Galeno", "APROSS", "Medifé", "Sancor Salud", "Particular", "Otra"];

const form = reactive({
  nombre: "",
  dni: "",
  estudio: "",
  fecha: "",
  obraSocial: "",
  telefono: "",
});

const errors = reactive<Record<string, string>>({});
const submitted = ref<typeof form | null>(null);

function validate(): boolean {
  const e: Record<string, string> = {};
  if (form.nombre.length < 3) e.nombre = "Ingrese su nombre completo";
  if (form.dni.length < 7) e.dni = "Ingrese un DNI válido";
  if (!form.estudio) e.estudio = "Seleccione un estudio";
  if (!form.fecha) e.fecha = "Seleccione una fecha";
  if (!form.obraSocial) e.obraSocial = "Seleccione obra social";
  if (form.telefono.length < 8) e.telefono = "Ingrese un número válido";
  Object.assign(errors, e);
  // Clear old errors
  for (const key of Object.keys(errors)) {
    if (!e[key]) delete errors[key];
  }
  return Object.keys(e).length === 0;
}

function onSubmit() {
  if (validate()) {
    submitted.value = { ...form };
  }
}

function reset() {
  submitted.value = null;
  form.nombre = "";
  form.dni = "";
  form.estudio = "";
  form.fecha = "";
  form.obraSocial = "";
  form.telefono = "";
}

function formatFecha(dateStr: string) {
  try {
    return format(new Date(dateStr), "PPP", { locale: es });
  } catch {
    return dateStr;
  }
}

function todayISO() {
  return new Date().toISOString().split("T")[0];
}
</script>

<template>
  <!-- Success -->
  <template v-if="submitted">
    <section class="py-16 md:py-24">
      <div class="container max-w-lg">
        <Card>
          <CardContent class="p-8 text-center space-y-4">
            <CheckCircle2 class="h-16 w-16 text-secondary mx-auto" />
            <h2 class="text-2xl font-bold">¡Turno Reservado!</h2>
            <p class="text-muted-foreground">Su turno ha sido registrado exitosamente.</p>
            <div class="text-left bg-muted rounded-lg p-4 space-y-2 text-sm">
              <p><strong>Paciente:</strong> {{ submitted.nombre }}</p>
              <p><strong>DNI:</strong> {{ submitted.dni }}</p>
              <p><strong>Estudio:</strong> {{ submitted.estudio }}</p>
              <p><strong>Fecha:</strong> {{ formatFecha(submitted.fecha) }}</p>
              <p><strong>Obra Social:</strong> {{ submitted.obraSocial }}</p>
              <p><strong>Teléfono:</strong> {{ submitted.telefono }}</p>
            </div>
            <Button variant="outline" @click="reset">Reservar otro turno</Button>
          </CardContent>
        </Card>
      </div>
    </section>
  </template>

  <!-- Form -->
  <template v-else>
    <section class="bg-muted/50 py-12 md:py-16">
      <div class="container text-center max-w-2xl">
        <h1 class="text-3xl md:text-4xl font-bold mb-3">Reservar Turno</h1>
        <p class="text-muted-foreground">Complete el formulario para solicitar un turno. Nos comunicaremos para confirmar disponibilidad.</p>
      </div>
    </section>

    <section class="py-12 md:py-20">
      <div class="container max-w-lg">
        <Card>
          <CardHeader>
            <CardTitle>Datos del turno</CardTitle>
          </CardHeader>
          <CardContent>
            <form @submit.prevent="onSubmit" class="space-y-5">
              <div class="space-y-2">
                <label class="text-sm font-medium">Nombre completo</label>
                <Input v-model="form.nombre" placeholder="Juan Pérez" />
                <p v-if="errors.nombre" class="text-sm text-destructive">{{ errors.nombre }}</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">DNI</label>
                <Input v-model="form.dni" placeholder="12345678" />
                <p v-if="errors.dni" class="text-sm text-destructive">{{ errors.dni }}</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">Tipo de estudio</label>
                <Select
                  v-model="form.estudio"
                  placeholder="Seleccione un estudio"
                  :options="estudios.map(e => ({ value: e, label: e }))"
                />
                <p v-if="errors.estudio" class="text-sm text-destructive">{{ errors.estudio }}</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">Fecha preferida</label>
                <Input v-model="form.fecha" type="date" :min="todayISO()" />
                <p v-if="errors.fecha" class="text-sm text-destructive">{{ errors.fecha }}</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">Obra social</label>
                <Select
                  v-model="form.obraSocial"
                  placeholder="Seleccione obra social"
                  :options="obrasSociales.map(o => ({ value: o, label: o }))"
                />
                <p v-if="errors.obraSocial" class="text-sm text-destructive">{{ errors.obraSocial }}</p>
              </div>

              <div class="space-y-2">
                <label class="text-sm font-medium">Teléfono de contacto</label>
                <Input v-model="form.telefono" placeholder="351 456 7890" />
                <p v-if="errors.telefono" class="text-sm text-destructive">{{ errors.telefono }}</p>
              </div>

              <Button type="submit" class="w-full" size="lg">Solicitar Turno</Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </section>
  </template>
</template>
