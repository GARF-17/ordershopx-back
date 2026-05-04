package com.ordershopx.backend.modules.categoria.service.impl;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;
import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import com.ordershopx.backend.modules.categoria.mapper.CategoriaMenuMapper;
import com.ordershopx.backend.modules.categoria.repository.CategoriaMenuRepository;
import com.ordershopx.backend.modules.categoria.service.ICategoriaMenuService;
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
public class CategoriaMenuServiceImpl implements ICategoriaMenuService {

    private final CategoriaMenuRepository categoriaRepository;
    private final CategoriaMenuMapper categoriaMapper;
    private final RestauranteRepository restauranteRepository;
    private final IUsuarioService usuarioService;

    // 🔐 Obtener usuario autenticado
    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // 🍽️ Obtener restaurante del usuario
    private Restaurante getRestaurante(Usuario usuario) {
        return restauranteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));
    }

    // ================================
    // CREAR
    // ================================
    @Override
    @Transactional
    public CategoriaMenuResponseDTO crearCategoria(CategoriaMenuRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        log.info("event=crear_categoria_start usuario={} nombre={}",
                usuario.getCorreoElectronico(), request.getNombre());

        if (categoriaRepository.existsByRestaurante_IdUsuarioAndNombreIgnoreCaseAndEliminadoEnIsNull(
                restaurante.getIdUsuario(),
                request.getNombre()
        )) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }

        CategoriaMenu categoria = categoriaMapper.toEntity(request);
        categoria.setRestaurante(restaurante);

        categoriaRepository.save(categoria);

        log.info("event=crear_categoria_success usuario={} categoriaId={}",
                usuario.getCorreoElectronico(), categoria.getIdCategoria());

        return categoriaMapper.toResponse(categoria);
    }

    // ================================
    // LISTAR
    // ================================
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaMenuResponseDTO> listarMisCategorias() {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        log.info("event=listar_categorias usuario={}", usuario.getCorreoElectronico());

        return categoriaRepository
                .findByRestaurante_IdUsuarioAndEliminadoEnIsNullOrderByOrdenVisualAsc(
                        restaurante.getIdUsuario()
                )
                .stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    // ================================
    // ACTUALIZAR
    @Override
    @Transactional
    public CategoriaMenuResponseDTO actualizarCategoria(UUID idCategoria, CategoriaMenuRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        log.info("event=actualizar_categoria_start usuario={} categoriaId={}",
                usuario.getCorreoElectronico(), idCategoria);

        CategoriaMenu categoria = categoriaRepository
                .findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(
                        idCategoria,
                        restaurante.getIdUsuario()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setNombre(request.getNombre());
        categoria.setOrdenVisual(request.getOrdenVisual());

        categoriaRepository.save(categoria);

        log.info("event=actualizar_categoria_success usuario={} categoriaId={}",
                usuario.getCorreoElectronico(), idCategoria);

        return categoriaMapper.toResponse(categoria);
    }

    // ELIMINAR
    @Override
    @Transactional
    public void eliminarCategoria(UUID idCategoria) {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getRestaurante(usuario);

        log.info("event=eliminar_categoria_start usuario={} categoriaId={}",
                usuario.getCorreoElectronico(), idCategoria);

        CategoriaMenu categoria = categoriaRepository
                .findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(
                        idCategoria,
                        restaurante.getIdUsuario()
                )
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setEliminadoEn(OffsetDateTime.now());

        categoriaRepository.save(categoria);

        log.info("event=eliminar_categoria_success usuario={} categoriaId={}",
                usuario.getCorreoElectronico(), idCategoria);
    }
}