package com.ordershopx.backend.modules.usuario.service;

import com.ordershopx.backend.modules.usuario.entity.Usuario;

import java.util.UUID;

public interface IUsuarioService {

    Usuario crearUsuario(Usuario usuario);
    Usuario obtenerPorCorreo(String correoElectronico);
    void validarDisponibilidad(String correo, String dni, String telefono);
    Usuario obtenerPorId(UUID usuarioId);
}
