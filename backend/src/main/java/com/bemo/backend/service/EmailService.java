package com.bemo.backend.service;

import com.bemo.backend.model.Turno;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
// @Async removido: el error debe propagarse al controller para informar al frontend
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${ecomed.mail.from:ECOMED <noreply@ecomed.com>}")
    private String fromAddress;

    @Value("${ecomed.app.url:http://localhost:8080}")
    private String appUrl;

    private static final DateTimeFormatter FMT_FECHA =
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));
    private static final DateTimeFormatter FMT_HORA =
        DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Envía email de confirmación al paciente.
     * Lanza RuntimeException si falla para que el controller lo informe al frontend.
     */
    public void enviarConfirmacionTurno(Turno turno) {
        String email = turno.getPaciente().getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Turno {}: paciente sin email registrado, se omite envío", turno.getId());
            return;
        }

        try {
            String fecha    = turno.getFechaHora().format(FMT_FECHA);
            String hora     = turno.getFechaHora().format(FMT_HORA);
            String paciente = turno.getPaciente().getNombre() + " " + turno.getPaciente().getApellido();
            String doctor   = "Dr/a. " + turno.getProfesional().getApellido() + ", " + turno.getProfesional().getNombre();
            String sucursal = turno.getSucursal().getNombre();
            String estudio  = turno.getEstudio() != null ? turno.getEstudio().getNombre() : "Consulta / Estudio";
            String urlConf  = appUrl + "/api/public/confirmacion/" + turno.getId() + "/confirmar";
            String urlCanc  = appUrl + "/api/public/confirmacion/" + turno.getId() + "/cancelar";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(email);
            helper.setSubject("Recordatorio de turno – " + sucursal + " · " + fecha);
            helper.setText(buildHtml(paciente, fecha, hora, doctor, sucursal, estudio, urlConf, urlCanc), true);

            mailSender.send(message);
            log.info("Confirmación enviada a {} para turno {}", email, turno.getId());
        } catch (Exception e) {
            log.error("Error al enviar email para turno {}: {}", turno.getId(), e.getMessage());
            throw new RuntimeException("No se pudo enviar el email: " + e.getMessage());
        }
    }

    private String buildHtml(String paciente, String fecha, String hora,
                              String doctor, String sucursal, String estudio,
                              String urlConf, String urlCanc) {
        return """
        <!DOCTYPE html>
        <html lang="es">
        <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
        <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;">
          <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f7fb;padding:32px 0;">
            <tr><td align="center">
              <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,.08);">

                <!-- Header -->
                <tr>
                  <td style="background:#1d4ed8;padding:28px 32px;">
                    <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:700;letter-spacing:-0.5px;">
                      🏥 ECOMED
                    </h1>
                    <p style="margin:4px 0 0;color:#93c5fd;font-size:13px;">Sistema de Imagen Médica</p>
                  </td>
                </tr>

                <!-- Body -->
                <tr>
                  <td style="padding:32px;">
                    <p style="margin:0 0 8px;font-size:16px;color:#374151;">Hola, <strong>%s</strong></p>
                    <p style="margin:0 0 24px;font-size:15px;color:#6b7280;">
                      Te recordamos que tenés un turno programado en nuestra clínica.
                    </p>

                    <!-- Tarjeta de turno -->
                    <table width="100%%" cellpadding="0" cellspacing="0"
                      style="background:#f0f6ff;border:1px solid #bfdbfe;border-radius:10px;margin-bottom:28px;">
                      <tr><td style="padding:20px 24px;">
                        <table width="100%%" cellpadding="6" cellspacing="0">
                          <tr>
                            <td style="color:#6b7280;font-size:13px;width:120px;">📅 Fecha</td>
                            <td style="color:#1e3a8a;font-size:14px;font-weight:700;text-transform:capitalize;">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#6b7280;font-size:13px;">🕐 Hora</td>
                            <td style="color:#1e3a8a;font-size:14px;font-weight:700;">%s hs</td>
                          </tr>
                          <tr>
                            <td style="color:#6b7280;font-size:13px;">👨‍⚕️ Profesional</td>
                            <td style="color:#111827;font-size:14px;">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#6b7280;font-size:13px;">🔬 Estudio</td>
                            <td style="color:#111827;font-size:14px;">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#6b7280;font-size:13px;">📍 Sede</td>
                            <td style="color:#111827;font-size:14px;font-weight:600;">%s</td>
                          </tr>
                        </table>
                      </td></tr>
                    </table>

                    <!-- Botones -->
                    <p style="margin:0 0 16px;font-size:14px;color:#374151;text-align:center;">
                      Por favor confirmá o cancelá tu turno:
                    </p>
                    <table width="100%%" cellpadding="0" cellspacing="0">
                      <tr>
                        <td align="center" style="padding:0 8px 0 0;">
                          <a href="%s"
                            style="display:inline-block;background:#16a34a;color:#ffffff;font-size:15px;font-weight:700;
                                   text-decoration:none;padding:14px 32px;border-radius:8px;width:100%%;text-align:center;
                                   box-sizing:border-box;">
                            ✅ Confirmar turno
                          </a>
                        </td>
                        <td align="center" style="padding:0 0 0 8px;">
                          <a href="%s"
                            style="display:inline-block;background:#dc2626;color:#ffffff;font-size:15px;font-weight:700;
                                   text-decoration:none;padding:14px 32px;border-radius:8px;width:100%%;text-align:center;
                                   box-sizing:border-box;">
                            ❌ Cancelar turno
                          </a>
                        </td>
                      </tr>
                    </table>

                    <p style="margin:24px 0 0;font-size:12px;color:#9ca3af;text-align:center;">
                      Si ya confirmaste por otro medio o tenés alguna consulta,<br>
                      comunicate con nosotros al momento de recibir este mensaje.
                    </p>
                  </td>
                </tr>

                <!-- Footer -->
                <tr>
                  <td style="background:#f9fafb;padding:16px 32px;border-top:1px solid #e5e7eb;">
                    <p style="margin:0;font-size:11px;color:#9ca3af;text-align:center;">
                      Este correo fue enviado automáticamente por ECOMED · Sistema de Gestión Médica.<br>
                      No respondas a este email.
                    </p>
                  </td>
                </tr>

              </table>
            </td></tr>
          </table>
        </body>
        </html>
        """.formatted(paciente, fecha, hora, doctor, estudio, sucursal, urlConf, urlCanc);
    }
}
