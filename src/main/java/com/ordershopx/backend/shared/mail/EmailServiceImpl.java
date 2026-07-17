package com.ordershopx.backend.shared.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
            // Usamos MimeMessage para permitir formato HTML
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("¡Te han invitado a unirte a OrderShopX!");

            String linkActivacion = deepLinkApp + "?token=" + token;

            String htmlBody = "<div style='font-family: Arial, sans-serif; background-color: #0B0B0B; color: #FFFFFF; padding: 30px; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h1 style='color: #10B981; text-align: center;'>¡Invitación a OrderShopX!</h1>"
                    + "<p style='font-size: 16px;'>Hola,</p>"
                    + "<p style='font-size: 16px;'>Has sido invitado para unirte al equipo con el rol de: <strong style='color: #8B5CF6;'>" + rol + "</strong>.</p>"
                    + "<p style='font-size: 16px;'>Para completar tu registro y activar tu cuenta, haz clic en el siguiente botón desde tu celular:</p>"
                    + "<div style='text-align: center; margin: 35px 0;'>"
                    + "<a href='" + linkActivacion + "' style='background-color: #8B5CF6; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block;'>Abrir App y Activar Cuenta</a>"
                    + "</div>"
                    + "<p style='font-size: 16px;'>Una vez que la aplicación se abra, te pedirá el siguiente PIN de seguridad:</p>"
                    + "<div style='background-color: #1A1A1A; padding: 20px; text-align: center; border-radius: 8px; margin: 25px 0; border: 1px solid #2A2A2A;'>"
                    + "<span style='font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #10B981;'>" + pin + "</span>"
                    + "</div>"
                    + "<hr style='border-color: #2A2A2A; margin-top: 30px;' />"
                    + "<p style='font-size: 12px; color: #8E8E93; text-align: center;'>OrderShopX - Transformando la gestión de tu restaurante</p>"
                    + "</div>";

            helper.setText(htmlBody, true);
            mailSender.send(mensaje);

            log.info("Correo de invitación HTML con botón enviado exitosamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error al enviar el correo HTML a {}: {}", destinatario, e.getMessage());
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

            helper.setText(htmlBody, true);

            mailSender.send(mensaje);
            log.info("Correo de activación HTML enviado exitosamente a: {}", destinatario);

        } catch (Exception e) {
            log.error("Error al enviar el correo de activación a {}: {}", destinatario, e.getMessage());
        }
    }
}