<script setup lang="ts">
import { RouterLink } from "vue-router";
import Button from "@/components/ui/Button.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import Accordion from "@/components/ui/Accordion.vue";
import AccordionItem from "@/components/ui/AccordionItem.vue";
import AccordionTrigger from "@/components/ui/AccordionTrigger.vue";
import AccordionContent from "@/components/ui/AccordionContent.vue";
import {
  Stethoscope, Scan, BrainCircuit, Bone, Heart, Shield,
  Clock, Award, Zap, ChevronRight,
} from "lucide-vue-next";
import heroImage from "@/assets/hero-medical.jpg";

const services = [
  { icon: Stethoscope, name: "Ecografía", desc: "Diagnóstico por ultrasonido de alta resolución" },
  { icon: Heart, name: "Mamografía", desc: "Mamografía digital de alta definición" },
  { icon: Scan, name: "Ecografía Ginecológica", desc: "Estudio del aparato reproductor femenino, útero y ovarios" },
  { icon: BrainCircuit, name: "Ecografía Obstétrica", desc: "Control y seguimiento del embarazo en todas sus etapas" },
  { icon: Zap, name: "Colposcopía", desc: "Examen de cuello uterino para detección precoz de lesiones" },
  { icon: Bone, name: "Pelvimetría", desc: "Medición de la pelvis para evaluación obstétrica" },
];

const whyUs = [
  { icon: Shield, title: "Tecnología Avanzada", desc: "Equipamiento de última generación para diagnósticos precisos y confiables." },
  { icon: Award, title: "Profesionales Especializados", desc: "Equipo médico con amplia experiencia en diagnóstico por imágenes." },
  { icon: Clock, title: "Resultados Rápidos", desc: "Entrega de informes en 24-48 hs con acceso digital desde el portal." },
];

const faqs = [
  { q: "¿Cómo debo prepararme para un estudio?", a: "La preparación varía según el tipo de estudio. Al confirmar su turno, le enviaremos las indicaciones específicas. Para ecografías abdominales se requiere ayuno de 6 horas." },
  { q: "¿Cuánto tardan los resultados?", a: "Los informes están disponibles en 24 a 48 horas hábiles. Puede descargarlos desde el Portal del Paciente o retirarlos en recepción." },
  { q: "¿Qué obras sociales aceptan?", a: "Trabajamos con las principales obras sociales y prepagas: OSDE, Swiss Medical, Galeno, INSTITUTO, entre otras. Consulte la lista completa en recepción." },
  { q: "¿Necesito orden médica?", a: "Sí, para realizar cualquier estudio de diagnóstico por imágenes es necesario presentar la orden médica correspondiente al momento del turno." },
  { q: "¿Puedo reservar turno online?", a: "Sí, puede reservar su turno desde nuestra sección Reservar Turno o comunicándose telefónicamente al 0299 456-8800." },
];
</script>

<template>
  <!-- Hero -->
  <section class="relative min-h-[70vh] flex items-center">
    <img :src="heroImage" alt="Equipamiento médico moderno" class="absolute inset-0 w-full h-full object-cover" />
    <div class="absolute inset-0 bg-foreground/60" />
    <div class="container relative z-10 py-20">
      <div class="max-w-2xl space-y-6">
        <h1 class="text-4xl md:text-5xl lg:text-6xl font-bold text-primary-foreground leading-tight">
          Tu salud en las mejores manos
        </h1>
        <p class="text-lg md:text-xl text-primary-foreground/85">
          Centro de diagnóstico por imágenes con tecnología de última generación y profesionales especializados.
        </p>
        <div class="flex flex-wrap gap-3">
          <RouterLink to="/reservar">
            <Button size="lg" class="text-base">Reservar Turno</Button>
          </RouterLink>
          <RouterLink to="/portal">
            <Button size="lg" variant="outline" class="text-base bg-primary-foreground/10 border-primary-foreground/30 text-primary-foreground hover:bg-primary-foreground/20 hover:text-primary-foreground">
              Portal Paciente
            </Button>
          </RouterLink>
        </div>
      </div>
    </div>
  </section>

  <!-- Services -->
  <section class="py-16 md:py-24">
    <div class="container">
      <div class="text-center max-w-2xl mx-auto mb-12">
        <h2 class="text-3xl font-bold mb-3">Nuestros Servicios</h2>
        <p class="text-muted-foreground">Ofrecemos una amplia gama de estudios de diagnóstico por imágenes con la más alta calidad.</p>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <Card v-for="s in services" :key="s.name" class="group hover:shadow-md transition-shadow">
          <CardContent class="p-6 flex items-start gap-4">
            <div class="rounded-lg bg-accent p-3 shrink-0">
              <component :is="s.icon" class="h-6 w-6 text-accent-foreground" />
            </div>
            <div class="space-y-1">
              <h3 class="font-semibold">{{ s.name }}</h3>
              <p class="text-sm text-muted-foreground">{{ s.desc }}</p>
              <RouterLink to="/reservar" class="inline-flex items-center text-sm text-primary font-medium mt-1 hover:underline">
                Reservar <ChevronRight class="h-3 w-3 ml-1" />
              </RouterLink>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>

  <!-- Why Us -->
  <section class="py-16 md:py-24 bg-muted/50">
    <div class="container">
      <h2 class="text-3xl font-bold text-center mb-12">¿Por qué elegirnos?</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <div v-for="item in whyUs" :key="item.title" class="text-center space-y-3">
          <div class="inline-flex rounded-full bg-primary/10 p-4">
            <component :is="item.icon" class="h-8 w-8 text-primary" />
          </div>
          <h3 class="text-lg font-semibold">{{ item.title }}</h3>
          <p class="text-sm text-muted-foreground max-w-xs mx-auto">{{ item.desc }}</p>
        </div>
      </div>
    </div>
  </section>

  <!-- FAQ -->
  <section class="py-16 md:py-24">
    <div class="container max-w-3xl">
      <h2 class="text-3xl font-bold text-center mb-10">Preguntas Frecuentes</h2>
      <Accordion class="w-full">
        <AccordionItem v-for="(faq, i) in faqs" :key="i" :value="`faq-${i}`">
          <AccordionTrigger :value="`faq-${i}`" class="text-left">{{ faq.q }}</AccordionTrigger>
          <AccordionContent :value="`faq-${i}`">{{ faq.a }}</AccordionContent>
        </AccordionItem>
      </Accordion>
    </div>
  </section>
</template>
