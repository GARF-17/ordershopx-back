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
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

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
    private final RestauranteRepository restauranteRepository;
    private final IUsuarioService usuarioService;

    // Usuario autenticado
    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // Restaurante del usuario
    private Restaurante getRestaurante(Usuario usuario) {
        return restauranteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));
    }

    // Validar categoría del restaurante
    private CategoriaMenu getCategoria(UUID idCategoria, UUID idRestaurante) {
        return categoriaRepository
                .findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(
                        idCategoria,
                        idRestaurante
                )
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    // CREAR
    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        log.info("event=crear_producto usuario={} nombre={}",
                usuario.getCorreoElectronico(), request.getNombre());

        CategoriaMenu categoria = getCategoria(
                request.getIdCategoria(),
                restaurante.getIdUsuario()
        );

        // Validar duplicado
        if (productoRepository.existsByCategoria_IdCategoriaAndNombreIgnoreCaseAndEliminadoEnIsNull(
                categoria.getIdCategoria(),
                request.getNombre()
        )) {
            throw new ConflictException("Ya existe un producto con ese nombre en la categoría");
        }

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);

        // 🔥 EL ARREGLO ESTÁ AQUÍ: Forzamos a que el producto nazca disponible para los clientes
        producto.setEstaDisponible(true);

        productoRepository.save(producto);

        log.info("event=crear_producto_success productoId={}", producto.getIdProducto());

        return productoMapper.toResponse(producto);
    }

    // LISTAR MIS PRODUCTOS
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarMisProductos() {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        return productoRepository
                .findByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNullOrderByNombreAsc(
                        restaurante.getIdUsuario()
                )
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    // LISTAR POR CATEGORÍA
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarPorCategoria(UUID idCategoria) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        // valida que la categoría exista y pertenezca al restaurante
        getCategoria(idCategoria, restaurante.getIdUsuario());

        return productoRepository
                .findByCategoria_IdCategoriaAndEliminadoEnIsNullOrderByNombreAsc(idCategoria)
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    // LISTAR PRODUCTOS PARA CLIENTES
    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> listarProductosCliente(UUID idRestaurante) {

        return productoRepository
                .findByCategoria_Restaurante_IdUsuarioAndEstaDisponibleTrueAndEliminadoEnIsNullOrderByNombreAsc(
                        idRestaurante
                )
                .stream()
                .map(productoMapper::toResponse)
                .toList();
    }

    // ACTUALIZAR
    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(UUID idProducto, ProductoRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        Producto producto = productoRepository
                .findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(
                        idProducto,
                        restaurante.getIdUsuario()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoMapper.updateFromDto(request, producto);

        productoRepository.save(producto);

        return productoMapper.toResponse(producto);
    }

    // ELIMINAR
    @Override
    @Transactional
    public void eliminarProducto(UUID idProducto) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        Producto producto = productoRepository
                .findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(
                        idProducto,
                        restaurante.getIdUsuario()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setEliminadoEn(OffsetDateTime.now());

        productoRepository.save(producto);
    }

    // DISPONIBILIDAD
    @Override
    @Transactional
    public void cambiarDisponibilidad(UUID idProducto, Boolean disponible) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        Producto producto = productoRepository
                .findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(
                        idProducto,
                        restaurante.getIdUsuario()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setEstaDisponible(disponible);

        productoRepository.save(producto);
    }
}