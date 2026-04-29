package com.ordershopx.backend.modules.usuario.service.impl;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Usuario crearUsuario(Usuario usuario) {

        log.info("event=crear_usuario_start correoElectronico={}", usuario.getCorreoElectronico());

        if (usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())) {

            log.warn("event=crear_usuario_conflict reason=correoElectronico_already_exists correoElectronico={}",
                    usuario.getCorreoElectronico());

            throw new ConflictException("El correo electrónico ya está registrado.");
        }

        // Guardar usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("event=crear_usuario_success usuarioId={}", usuarioGuardado.getUsuarioId());

        return usuarioGuardado;
    }

    @Override
    public Usuario obtenerPorCorreo(String correoElectronico) {

        log.info("event=obtener_usuario correoElectronico={}", correoElectronico);

        return usuarioRepository.findByCorreoElectronico(correoElectronico)
                .orElseThrow(() -> {
                    log.warn("event=usuario_not_found correoElectronico={}", correoElectronico);
                    return new ResourceNotFoundException("Usuario no encontrado.");
                });
    }

    @Override
    public boolean existePorCorreo(String correoElectronico) {

        log.info("event=existe_usuario correoElectronico={}", correoElectronico);

        return usuarioRepository.existsByCorreoElectronico(correoElectronico);
    }
}