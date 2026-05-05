package com.ordershopx.backend.modules.producto.mapper;

import com.ordershopx.backend.modules.producto.dto.request.ProductoRequestDTO;
import com.ordershopx.backend.modules.producto.dto.response.ProductoClienteDTO;
import com.ordershopx.backend.modules.producto.dto.response.ProductoResponseDTO;
import com.ordershopx.backend.modules.producto.entity.Producto;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "eliminadoEn", ignore = true)
    Producto toEntity(ProductoRequestDTO dto);

    @Mapping(target = "idCategoria", source = "categoria.idCategoria")
    ProductoResponseDTO toResponse(Producto entity);

    @Mapping(target = "disponible", source = "estaDisponible")
    ProductoClienteDTO toCliente(Producto entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idProducto", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "eliminadoEn", ignore = true)
    void updateFromDto(ProductoRequestDTO dto, @MappingTarget Producto entity);
}