package com.ordershopx.backend.modules.usuario.service.impl;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

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

}