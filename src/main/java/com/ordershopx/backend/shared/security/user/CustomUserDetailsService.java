package com.ordershopx.backend.shared.security.user;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correoElectronico)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreoElectronico(correoElectronico)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado")
                );

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(usuario.getCorreoElectronico())
                .password(usuario.getClaveHash())
                .roles(usuario.getRol().name())
                .build();
    }
}