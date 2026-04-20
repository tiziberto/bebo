<script setup lang="ts">
import { ref } from "vue";
import Button from "@/components/ui/Button.vue";
import Input from "@/components/ui/Input.vue";
import Textarea from "@/components/ui/Textarea.vue";
import Card from "@/components/ui/Card.vue";
import CardContent from "@/components/ui/CardContent.vue";
import CardHeader from "@/components/ui/CardHeader.vue";
import CardTitle from "@/components/ui/CardTitle.vue";
import { MapPin, Phone, Mail, Clock, CheckCircle2 } from "lucide-vue-next";
import { useToastStore } from "@/stores/toast";

const toast = useToastStore();
const sent = ref(false);

function handleSubmit() {
  sent.value = true;
  toast.show({ title: "Consulta enviada", description: "Nos comunicaremos a la brevedad." });
}
</script>

<template>
  <section class="bg-muted/50 py-12 md:py-16">
    <div class="container text-center max-w-2xl">
      <h1 class="text-3xl md:text-4xl font-bold mb-3">Contacto</h1>
      <p class="text-muted-foreground">Estamos para ayudarte. Comunicate con nosotros por cualquiera de nuestros canales.</p>
    </div>
  </section>

  <section class="py-12 md:py-20">
    <div class="container">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-10">
        <!-- Info -->
        <div class="space-y-6">
          <Card>
            <CardContent class="p-6 space-y-5">
              <div class="flex items-start gap-3">
                <MapPin class="h-5 w-5 text-primary mt-0.5 shrink-0" />
                <div>
                  <h3 class="font-semibold text-sm">Dirección</h3>
                  <p class="text-sm text-muted-foreground">Córdoba 180 Q8300, Q8300 Neuquén</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Phone class="h-5 w-5 text-primary mt-0.5 shrink-0" />
                <div>
                  <h3 class="font-semibold text-sm">Teléfono</h3>
                  <p class="text-sm text-muted-foreground">0299 456-8800</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Mail class="h-5 w-5 text-primary mt-0.5 shrink-0" />
                <div>
                  <h3 class="font-semibold text-sm">Email</h3>
                  <p class="text-sm text-muted-foreground">info@ecomed.com.ar</p>
                </div>
              </div>
              <div class="flex items-start gap-3">
                <Clock class="h-5 w-5 text-primary mt-0.5 shrink-0" />
                <div>
                  <h3 class="font-semibold text-sm">Horarios de atención</h3>
                  <p class="text-sm text-muted-foreground">Lunes a Viernes: 7:00 - 20:00</p>
                  <p class="text-sm text-muted-foreground">Sábados: 8:00 - 14:00</p>
                </div>
              </div>
            </CardContent>
          </Card>

          <!-- Map placeholder -->
          <div class="rounded-lg overflow-hidden border bg-muted h-56 flex items-center justify-center">
            <div class="text-center text-muted-foreground">
              <MapPin class="h-10 w-10 mx-auto mb-2 opacity-40" />
              <p class="text-sm">Córdoba 180 Q8300, Neuquén</p>
            </div>
          </div>
        </div>

        <!-- Form -->
        <Card>
          <CardHeader>
            <CardTitle>Envianos tu consulta</CardTitle>
          </CardHeader>
          <CardContent>
            <div v-if="sent" class="text-center py-8 space-y-3">
              <CheckCircle2 class="h-14 w-14 text-secondary mx-auto" />
              <h3 class="font-semibold text-lg">¡Mensaje enviado!</h3>
              <p class="text-sm text-muted-foreground">Nos comunicaremos a la brevedad.</p>
              <Button variant="outline" @click="sent = false">Enviar otra consulta</Button>
            </div>
            <form v-else @submit.prevent="handleSubmit" class="space-y-4">
              <div class="space-y-2">
                <label class="text-sm font-medium">Nombre</label>
                <Input placeholder="Su nombre" required />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium">Email</label>
                <Input type="email" placeholder="correo@ejemplo.com" required />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium">Teléfono</label>
                <Input placeholder="0299 456-8800" />
              </div>
              <div class="space-y-2">
                <label class="text-sm font-medium">Mensaje</label>
                <Textarea placeholder="Escriba su consulta..." :rows="4" required />
              </div>
              <Button type="submit" class="w-full">Enviar Consulta</Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>
</template>
