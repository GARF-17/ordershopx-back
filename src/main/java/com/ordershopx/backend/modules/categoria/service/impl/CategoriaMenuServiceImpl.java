package com.ordershopx.backend.modules.categoria.service.impl;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;
import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import com.ordershopx.backend.modules.categoria.mapper.CategoriaMenuMapper;
import com.ordershopx.backend.modules.categoria.repository.CategoriaMenuRepository;
import com.ordershopx.backend.modules.categoria.service.ICategoriaMenuService;
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
public class CategoriaMenuServiceImpl implements ICategoriaMenuService {

    private final CategoriaMenuRepository categoriaRepository;
    private final CategoriaMenuMapper categoriaMapper;
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
                throw new UnauthorizedException("Permisos insuficientes. Solo OWNER o ADMIN_LOCAL pueden modificar el catálogo.");
            }
        }
        return asignacion;
    }

    @Override
    @Transactional
    public CategoriaMenuResponseDTO crearCategoria(CategoriaMenuRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        if (categoriaRepository.existsByRestaurante_IdUsuarioAndNombreIgnoreCaseAndEliminadoEnIsNull(
                restaurante.getIdUsuario(), request.getNombre())) {
            throw new ConflictException("Ya existe una categoría con ese nombre");
        }

        CategoriaMenu categoria = categoriaMapper.toEntity(request);
        categoria.setRestaurante(restaurante);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaMenuResponseDTO> listarMisCategorias() {
        Usuario usuario = getUsuarioAutenticado();
        // Todos pueden listar, no requiere admin
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false).getRestaurante();

        return categoriaRepository.findByRestaurante_IdUsuarioAndEliminadoEnIsNullOrderByOrdenVisualAsc(restaurante.getIdUsuario())
                .stream().map(categoriaMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CategoriaMenuResponseDTO actualizarCategoria(UUID idCategoria, CategoriaMenuRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        CategoriaMenu categoria = categoriaRepository.findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(idCategoria, restaurante.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setNombre(request.getNombre());
        categoria.setOrdenVisual(request.getOrdenVisual());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminarCategoria(UUID idCategoria) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true).getRestaurante();

        CategoriaMenu categoria = categoriaRepository.findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(idCategoria, restaurante.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setEliminadoEn(OffsetDateTime.now());
        categoriaRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaMenuResponseDTO> listarCategoriasPorRestaurante(UUID idRestaurante) {
        return categoriaRepository.listarActivasPorRestaurante(idRestaurante)
                .stream().map(categoriaMapper::toResponse).toList();
    }
}