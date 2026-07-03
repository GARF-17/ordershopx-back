package com.ordershopx.backend.shared.mail;

public interface IEmailService {
    void enviarCorreoInvitacion(String destinatario, String token, String pin, String rol);

    // ==========================================
    // 2. NUEVO MÉTODO HTML (Activación Restaurante)
    // ==========================================
    void enviarCorreoActivacion(String destinatario, String nombreEncargado, String nombreRestaurante, String pin);
}