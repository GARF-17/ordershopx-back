package com.ordershopx.backend.modules.usuario.service.impl;

import com.ordershopx.backend.modules.usuario.dto.response.UsuarioResponseDTO;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.mapper.UsuarioMapper;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper; // Inyectamos tu Mapper para los DTOs del Admin

    @Override
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        log.info("event=crear_usuario_start correo={}", usuario.getCorreoElectronico());

        validarDisponibilidad(usuario.getCorreoElectronico(), usuario.getDni(), usuario.getTelefono());

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public void validarDisponibilidad(String correo, String dni, String telefono) {

        if (correo != null && usuarioRepository.existsByCorreoElectronico(correo)) {
            throw new ConflictException("El correo electrónico ya está registrado.");
        }

        if (dni != null && usuarioRepository.existsByDni(dni)) {
            throw new ConflictException("El DNI ya está registrado.");
        }

        if (telefono != null && usuarioRepository.existsByTelefono(telefono)) {
            throw new ConflictException("El número de teléfono ya está en uso.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorCorreo(String correoElectronico) {
        return usuarioRepository.findByCorreoElectronico(correoElectronico)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }

    // ==========================================
    // MÉTODOS AGREGADOS PARA EL PANEL DE ADMIN
    // ==========================================

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodosLosUsuarios() {
        log.info("event=service_listar_todos_usuarios");
        List<Usuario> usuarios = usuarioRepository.findAll();

        // Convertimos la lista de entidades a DTOs usando tu mapper
        return usuarios.stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cambiarEstado(UUID idUsuario, Boolean activo) {
        log.info("event=service_cambiar_estado_usuario idUsuario={} activo={}", idUsuario, activo);

        // Buscamos el usuario reutilizando tu método (lanza 404 si no existe)
        Usuario usuario = obtenerPorId(idUsuario);

        // Cambiamos el estado (utiliza la propiedad 'estaActivo' que vimos en tus logs)
        usuario.setEstaActivo(activo);

        // Guardamos los cambios
        usuarioRepository.save(usuario);
    }
}