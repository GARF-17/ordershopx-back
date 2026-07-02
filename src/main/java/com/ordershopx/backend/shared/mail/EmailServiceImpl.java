package com.ordershopx.backend.shared.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
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
}