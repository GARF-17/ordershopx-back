package com.ordershopx.backend.modules.producto.service.impl;

import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import com.ordershopx.backend.modules.categoria.repository.CategoriaMenuRepository;
import com.ordershopx.backend.modules.producto.dto.request.ProductoRequestDTO;
import com.ordershopx.backend.modules.producto.dto.response.ProductoResponseDTO;
import com.ordershopx.backend.modules.producto.entity.Producto;
import com.ordershopx.backend.modules.producto.mapper.ProductoMapper;
import com.ordershopx.backend.modules.producto.repository.ProductoRepository;
import com.ordershopx.backend.modules.producto.service.IProductoService;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaMenuRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final IUsuarioService usuarioService;
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;

    private Usuario getUsuarioAutenticado() {
        return usuarioService.obtenerPorCorreo(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private UsuarioRestaurante getAsignacionYValidarPermisos(Usuario usuario, boolean requiereAdmin) {
        UsuarioRestaurante asignacion = usuarioRestauranteRepository.findFirstByUsuarioUsuarioIdAndEstaActivoTrue(usuario.getUsuarioId())
                .orElseThrow(() -> new UnauthorizedException("No estás asignado a ningún restaurante activo."));

        if (requiereAdmin) {
            RolRestaurante rol = asignacion.getRol();
            if (rol != RolRestaurante.OWNER && rol != RolRestaurante.ADMIN_LOCAL) {
                throw new UnauthorizedException("Permisos insuficientes. Solo OWNER o ADMIN_LOCAL pueden modificar productos.");
            }
        }
        return asignacion;
    }

    private CategoriaMenu getCategoria(UUID idCategoria, UUID idRestaurante) {
        return categoriaRepository.findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(idCategoria, idRestaurante)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada o no pertenece al restaurante"));
    }

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        CategoriaMenu categoria = getCategoria(request.getIdCategoria(), restaurante.getIdUsuario());

        if (productoRepository.existsByCategoria_IdCategoriaAndNombreIgnoreCaseAndEliminadoEnIsNull(categoria.getIdCategoria(), request.getNombre())) {
            throw new ConflictException("Ya existe un producto con ese nombre en la categoría");
        }

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        producto.setEstaDisponible(true);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarMisProductos() {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false).getRestaurante();

        return productoRepository.findByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNullOrderByNombreAsc(restaurante.getIdUsuario())
                .stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(UUID idCategoria) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false).getRestaurante();
        getCategoria(idCategoria, restaurante.getIdUsuario());

        return productoRepository.findByCategoria_IdCategoriaAndEliminadoEnIsNullOrderByNombreAsc(idCategoria)
                .stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosCliente(UUID idRestaurante) {
        return productoRepository.findByCategoria_Restaurante_IdUsuarioAndEstaDisponibleTrueAndEliminadoEnIsNullOrderByNombreAsc(idRestaurante)
                .stream().map(productoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(UUID idProducto, ProductoRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        Producto producto = productoRepository.findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(idProducto, restaurante.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoMapper.updateFromDto(request, producto);
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminarProducto(UUID idProducto) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        Producto producto = productoRepository.findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(idProducto, restaurante.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setEliminadoEn(OffsetDateTime.now());
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void cambiarDisponibilidad(UUID idProducto, Boolean disponible) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        Producto producto = productoRepository.findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(idProducto, restaurante.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setEstaDisponible(disponible);
        productoRepository.save(producto);
    }
}