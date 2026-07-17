package com.ordershopx.backend.shared.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.sender}")
    private String remitente;

    @Value("${app.mobile.deeplink}")
    private String deepLinkApp;

    @Override
    public void enviarCorreoInvitacion(String destinatario, String token, String pin, String rol) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(remitente);
            mensaje.setTo(destinatario);
            mensaje.setSubject("¡Te han invitado a unirte a OrderShopX!");

            String cuerpoMensaje = String.format(
                    "Hola,\n\n" +
                            "Has sido invitado para unirte a OrderShopX con el rol de: %s.\n\n" +
                            "Para completar tu registro, haz clic en el siguiente enlace:\n" +
                            "%s?token=%s\n\n" +
                            "Una vez que ingreses, el sistema te pedirá el siguiente PIN de seguridad:\n" +
                            "PIN: %s\n\n" +
                            "¡Bienvenido al equipo!\n" +
                            "El equipo de OrderShopX",
                    rol, deepLinkApp, token, pin
            );

            mensaje.setText(cuerpoMensaje);
            mailSender.send(mensaje);

            log.info("Correo de invitación con Enlace Mágico enviado exitosamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error al enviar el correo a {}: {}", destinatario, e.getMessage());
            // No lanzamos excepción para no romper la transacción de la BD si el correo falla
        }
    }

    @Override
    public void enviarCorreoActivacion(String destinatario, String nombreEncargado, String nombreRestaurante, String pin) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("¡Solicitud Aprobada! Activa tu restaurante en OrderShopX");

            String htmlBody = "<div style='font-family: Arial, sans-serif; background-color: #0B0B0B; color: #FFFFFF; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h1 style='color: #10B981; text-align: center;'>¡Bienvenido a OrderShopX!</h1>"
                    + "<p style='font-size: 16px;'>Hola <strong>" + nombreEncargado + "</strong>,</p>"
                    + "<p style='font-size: 16px;'>Nos complace informarte que la solicitud para <strong>" + nombreRestaurante + "</strong> ha sido aprobada.</p>"
                    + "<p style='font-size: 16px;'>Para activar tu cuenta y configurar tu contraseña, ingresa a la aplicación y usa el siguiente PIN de seguridad de 6 dígitos:</p>"
                    + "<div style='background-color: #1A1A1A; padding: 20px; text-align: center; border-radius: 8px; margin: 25px 0; border: 1px solid #2A2A2A;'>"
                    + "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #8B5CF6;'>" + pin + "</span>"
                    + "</div>"
                    + "<p style='font-size: 14px; color: #8E8E93;'>* Este PIN expirará en 7 días por motivos de seguridad.</p>"
                    + "<hr style='border-color: #2A2A2A; margin-top: 30px;' />"
                    + "<p style='font-size: 12px; color: #8E8E93; text-align: center;'>OrderShopX - Transformando la gestión de tu restaurante</p>"
                    + "</div>";

            helper.setText(htmlBody, true); // El "true" indica que el texto es HTML

            mailSender.send(mensaje);
            log.info("Correo de activación HTML enviado exitosamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error al enviar el correo de activación a {}: {}", destinatario, e.getMessage());
        }
    }
}