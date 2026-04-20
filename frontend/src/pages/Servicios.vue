<script setup lang="ts">
import { RouterLink } from "vue-router";
import Button from "@/components/ui/Button.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import { Stethoscope, Scan, BrainCircuit, Bone, Heart, Zap } from "lucide-vue-next";

const services = [
  {
    icon: Stethoscope, name: "Ecografía General",
    desc: "Estudios por ultrasonido para evaluar órganos internos, tiroides, partes blandas y sistema vascular. Procedimiento no invasivo, indoloro y sin radiación.",
  },
  {
    icon: Heart, name: "Mamografía Digital",
    desc: "Mamografía digital de alta definición para detección temprana de cáncer de mama. Incluye tomosíntesis mamaria para mayor precisión diagnóstica.",
  },
  {
    icon: Scan, name: "Ecografía Ginecológica",
    desc: "Evaluación del aparato reproductor femenino: útero, ovarios y trompas. Diagnóstico de quistes, miomas, endometriosis y otras patologías ginecológicas.",
  },
  {
    icon: BrainCircuit, name: "Ecografía Obstétrica",
    desc: "Control y seguimiento del embarazo en todas sus etapas. Incluye morfología fetal, Doppler placentario y medición de líquido amniótico.",
  },
  {
    icon: Zap, name: "Colposcopía",
    desc: "Examen detallado del cuello uterino, vagina y vulva mediante colposcopio. Indicada para seguimiento de Papanicolaou alterado y detección precoz de lesiones.",
  },
  {
    icon: Bone, name: "Pelvimetría",
    desc: "Medición y evaluación de las dimensiones de la pelvis materna para planificación obstétrica. Complemento fundamental en la atención del embarazo de alto riesgo.",
  },
];
</script>

<template>
  <section class="bg-muted/50 py-12 md:py-16">
    <div class="container text-center max-w-2xl">
      <h1 class="text-3xl md:text-4xl font-bold mb-3">Nuestros Servicios</h1>
      <p class="text-muted-foreground">Contamos con equipamiento de última generación para brindarle estudios de diagnóstico por imágenes con la máxima calidad y precisión.</p>
    </div>
  </section>

  <section class="py-12 md:py-20">
    <div class="container">
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card v-for="s in services" :key="s.name" class="hover:shadow-md transition-shadow">
          <CardContent class="p-6 flex gap-4">
            <div class="rounded-lg bg-accent p-3 shrink-0 h-fit">
              <component :is="s.icon" class="h-7 w-7 text-accent-foreground" />
            </div>
            <div class="space-y-2 flex-1">
              <h2 class="text-xl font-semibold">{{ s.name }}</h2>
              <p class="text-sm text-muted-foreground leading-relaxed">{{ s.desc }}</p>
              <RouterLink to="/reservar">
                <Button size="sm" class="mt-2">Reservar Turno</Button>
              </RouterLink>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>
</template>
