package com.bemo.backend.controller;

import com.bemo.backend.model.Turno;
import com.bemo.backend.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Endpoints públicos (sin JWT) para que el paciente confirme o cancele
 * su turno desde el link del email.
 */
@RestController
@RequestMapping("/api/public/confirmacion")
@RequiredArgsConstructor
public class ConfirmacionPublicaController {

    private final TurnoRepository turnoRepo;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM · HH:mm 'hs'", new Locale("es", "AR"));

    @GetMapping(value = "/{id}/confirmar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> confirmar(@PathVariable Long id) {
        return turnoRepo.findById(id).map(turno -> {
            if ("CANCELADO".equals(turno.getEstado())) {
                return ResponseEntity.ok(htmlRespuesta("⚠️ Turno ya cancelado",
                    "Este turno fue cancelado previamente.",
                    turno, "#f59e0b", false));
            }
            turno.setEstado("CONFIRMADO");
            turno.setUpdatedAt(LocalDateTime.now());
            turnoRepo.save(turno);
            return ResponseEntity.ok(htmlRespuesta("✅ Turno confirmado",
                "¡Gracias! Tu turno quedó <strong>confirmado</strong>. Te esperamos.",
                turno, "#16a34a", true));
        }).orElse(ResponseEntity.ok(htmlError("Turno no encontrado",
            "El turno que intentás confirmar no existe o fue eliminado.")));
    }

    @GetMapping(value = "/{id}/cancelar", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        return turnoRepo.findById(id).map(turno -> {
            if ("CONFIRMADO".equals(turno.getEstado())) {
                // Aún permitimos cancelar aunque esté confirmado
            }
            turno.setEstado("CANCELADO");
            turno.setUpdatedAt(LocalDateTime.now());
            turnoRepo.save(turno);
            return ResponseEntity.ok(htmlRespuesta("❌ Turno cancelado",
                "Tu turno fue <strong>cancelado</strong>. Si fue un error, comunicate con la clínica.",
                turno, "#dc2626", false));
        }).orElse(ResponseEntity.ok(htmlError("Turno no encontrado",
            "El turno que intentás cancelar no existe o fue eliminado.")));
    }

    // ── HTML helpers ──────────────────────────────────────────────────────────

    private String htmlRespuesta(String titulo, String mensaje, Turno turno,
                                  String color, boolean confirmado) {
        String fecha = turno.getFechaHora().format(FMT);
        String paciente = turno.getPaciente().getNombre() + " " + turno.getPaciente().getApellido();
        String doctor = "Dr/a. " + turno.getProfesional().getApellido();
        String sucursal = turno.getSucursal().getNombre();
        String emoji = confirmado ? "🗓️" : "📋";

        return """
        <!DOCTYPE html><html lang="es">
        <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
        <title>%s – ECOMED</title></head>
        <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="padding:48px 16px;">
            <tr><td align="center">
              <table width="480" cellpadding="0" cellspacing="0"
                style="background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 16px rgba(0,0,0,.1);">
                <tr><td style="background:%s;padding:32px;text-align:center;">
                  <div style="font-size:48px;margin-bottom:8px;">%s</div>
                  <h1 style="margin:0;color:#fff;font-size:22px;">%s</h1>
                </td></tr>
                <tr><td style="padding:32px;">
                  <p style="margin:0 0 20px;font-size:16px;color:#374151;text-align:center;">%s</p>
                  <table width="100%%" style="background:#f9fafb;border-radius:8px;padding:16px;border:1px solid #e5e7eb;">
                    <tr><td style="color:#6b7280;font-size:13px;padding:4px 0;">Paciente</td>
                        <td style="color:#111827;font-size:14px;font-weight:600;padding:4px 0;">%s</td></tr>
                    <tr><td style="color:#6b7280;font-size:13px;padding:4px 0;">Turno</td>
                        <td style="color:#111827;font-size:14px;text-transform:capitalize;padding:4px 0;">%s</td></tr>
                    <tr><td style="color:#6b7280;font-size:13px;padding:4px 0;">Profesional</td>
                        <td style="color:#111827;font-size:14px;padding:4px 0;">%s</td></tr>
                    <tr><td style="color:#6b7280;font-size:13px;padding:4px 0;">Sede</td>
                        <td style="color:#111827;font-size:14px;font-weight:600;padding:4px 0;">%s</td></tr>
                  </table>
                </td></tr>
                <tr><td style="background:#f9fafb;padding:16px 32px;border-top:1px solid #e5e7eb;">
                  <p style="margin:0;font-size:11px;color:#9ca3af;text-align:center;">
                    ECOMED · Sistema de Imagen Médica
                  </p>
                </td></tr>
              </table>
            </td></tr>
          </table>
        </body></html>
        """.formatted(titulo, color, emoji, titulo, mensaje, paciente, fecha, doctor, sucursal);
    }

    private String htmlError(String titulo, String mensaje) {
        return """
        <!DOCTYPE html><html lang="es">
        <head><meta charset="UTF-8"><title>%s – ECOMED</title></head>
        <body style="margin:0;padding:48px 16px;background:#f4f7fb;font-family:Arial,sans-serif;text-align:center;">
          <div style="max-width:400px;margin:auto;background:#fff;border-radius:12px;padding:40px;
               box-shadow:0 2px 12px rgba(0,0,0,.08);">
            <div style="font-size:48px;">⚠️</div>
            <h2 style="color:#374151;">%s</h2>
            <p style="color:#6b7280;">%s</p>
          </div>
        </body></html>
        """.formatted(titulo, titulo, mensaje);
    }
}
