package com.ordershopx.backend.modules.cliente.service.impl;

import com.ordershopx.backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.PreferenciasRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.UbicacionRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.cliente.mapper.ClienteMapper;
import com.ordershopx.backend.modules.cliente.repository.ClienteRepository;
import com.ordershopx.backend.modules.cliente.service.IClienteService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements IClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final IUsuarioService usuarioService;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    @Override
    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=crear_cliente_start usuario={}", usuario.getCorreoElectronico());

        clienteRepository.findByUsuario(usuario).ifPresent(c -> {
            log.warn("event=crear_cliente_conflict usuario={}", usuario.getCorreoElectronico());
            throw new ConflictException("El cliente ya existe para este usuario.");
        });

        Cliente cliente = clienteMapper.toEntity(request);
        cliente.setUsuario(usuario);

        Cliente saved = clienteRepository.save(cliente);

        log.info("event=crear_cliente_success usuario={}", usuario.getCorreoElectronico());

        return clienteMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerMiCliente() {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=obtener_cliente_start usuario={}", usuario.getCorreoElectronico());

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> {
                    log.warn("event=cliente_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Cliente no encontrado");
                });

        log.info("event=obtener_cliente_success usuario={}", usuario.getCorreoElectronico());

        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public void actualizarUbicacion(UbicacionRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=actualizar_ubicacion_start usuario={}", usuario.getCorreoElectronico());

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> {
                    log.warn("event=cliente_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Cliente no encontrado");
                });

        cliente.setLatitud(request.getLatitud());
        cliente.setLongitud(request.getLongitud());

        clienteRepository.save(cliente);

        log.info("event=actualizar_ubicacion_success usuario={}", usuario.getCorreoElectronico());
    }

    @Override
    @Transactional
    public void crearDesdeRegister(Usuario usuario, String nombre, String apellido) {

        log.info("event=crear_cliente_register usuario={}", usuario.getCorreoElectronico());

        clienteRepository.findByUsuario(usuario).ifPresent(c -> {
            log.warn("event=cliente_ya_existe_register usuario={}", usuario.getCorreoElectronico());
            throw new ConflictException("El cliente ya existe");
        });

        Cliente cliente = Cliente.builder()
                .usuario(usuario)
                .nombre(nombre)
                .apellido(apellido)
                .build();

        clienteRepository.save(cliente);

        log.info("event=crear_cliente_register_success usuario={}", usuario.getCorreoElectronico());
    }

        @Override
        @Transactional
        public void actualizarPreferencias(PreferenciasRequestDTO request) {

            Usuario usuario = getUsuarioAutenticado();

            Cliente cliente = clienteRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

            cliente.setPreferenciasJson(request.getPreferenciasJson());

            clienteRepository.save(cliente);
        }
}