package com.ordershopx.backend.shared.mail;

public interface IEmailService {
    void enviarCorreoInvitacion(String destinatario, String token, String pin, String rol);
}