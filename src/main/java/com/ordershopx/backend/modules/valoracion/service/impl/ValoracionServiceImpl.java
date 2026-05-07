package com.ordershopx.backend.modules.valoracion.service.impl;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.cliente.repository.ClienteRepository;
import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.modules.pedido.repository.PedidoRepository;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.valoracion.dto.request.ValoracionRequestDTO;
import com.ordershopx.backend.modules.valoracion.dto.response.ValoracionResponseDTO;
import com.ordershopx.backend.modules.valoracion.entity.Valoracion;
import com.ordershopx.backend.modules.valoracion.mapper.ValoracionMapper;
import com.ordershopx.backend.modules.valoracion.repository.ValoracionRepository;
import com.ordershopx.backend.modules.valoracion.service.IValoracionService;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements IValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;

    private final ValoracionMapper valoracionMapper;

    @Override
    @Transactional
    public ValoracionResponseDTO registrarValoracion(
            ValoracionRequestDTO request
    ) {

        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido no encontrado")
                );

        // VALIDAR SI EL PEDIDO YA TIENE VALORACION
        if (valoracionRepository.existsByPedido_IdPedido(
                pedido.getIdPedido()
        )) {

            throw new IllegalStateException(
                    "Este pedido ya tiene una valoración registrada"
            );
        }

        // VALIDAR QUE EL PEDIDO ESTÉ COMPLETADO
        if (pedido.getEstado() != EstadoPedido.COMPLETADO) {

            throw new IllegalStateException(
                    "Solo se pueden valorar pedidos completados"
            );
        }

        Cliente cliente = pedido.getCliente();

        Restaurante restaurante = pedido.getRestaurante();

        Valoracion valoracion = Valoracion.builder()
                .pedido(pedido)
                .cliente(cliente)
                .restaurante(restaurante)
                .puntuacion(request.getPuntuacion())
                .comentario(request.getComentario())
                .build();

        Valoracion saved = valoracionRepository.save(valoracion);

        log.info(
                "event=valoracion_registrada idValoracion={} pedido={}",
                saved.getIdValoracion(),
                pedido.getIdPedido()
        );

        return valoracionMapper.toResponse(saved);
    }

    @Override
    public ValoracionResponseDTO obtenerValoracionPorId(
            UUID idValoracion
    ) {

        Valoracion valoracion = valoracionRepository.findById(idValoracion)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Valoración no encontrada")
                );

        return valoracionMapper.toResponse(valoracion);
    }

    @Override
    public ValoracionResponseDTO obtenerValoracionPorPedido(
            UUID idPedido
    ) {

        Valoracion valoracion = valoracionRepository
                .findByPedido_IdPedido(idPedido)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No existe valoración para este pedido"
                        )
                );

        return valoracionMapper.toResponse(valoracion);
    }

    @Override
    public List<ValoracionResponseDTO> listarValoracionesRestaurante(
            UUID idRestaurante
    ) {

        log.info(
                "event=listar_valoraciones_restaurante restaurante={}",
                idRestaurante
        );

        return valoracionRepository
                .findByRestaurante_IdUsuarioOrderByFechaCreacionDesc(
                        idRestaurante
                )
                .stream()
                .map(valoracionMapper::toResponse)
                .toList();
    }

    @Override
    public List<ValoracionResponseDTO> listarMisValoraciones() {

        log.info("event=listar_mis_valoraciones");

        return valoracionRepository
                .findAll()
                .stream()
                .map(valoracionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarValoracion(
            UUID idValoracion
    ) {

        Valoracion valoracion = valoracionRepository.findById(idValoracion)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Valoración no encontrada")
                );

        valoracionRepository.delete(valoracion);

        log.info(
                "event=valoracion_eliminada id={}",
                idValoracion
        );
    }
}