package com.ordershopx.backend.modules.usuario.service;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.dto.response.UsuarioResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IUsuarioService {

    Usuario crearUsuario(Usuario usuario);
    Usuario obtenerPorCorreo(String correoElectronico);
    void validarDisponibilidad(String correo, String dni, String telefono);
    Usuario obtenerPorId(UUID usuarioId);

    // Nuevos métodos agregados para el panel de administración
    List<UsuarioResponseDTO> listarTodosLosUsuarios();
    void cambiarEstado(UUID idUsuario, Boolean activo);
}