package com.ordershopx.backend.modules.onboarding.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRestauranteRequestDTO {

    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 11, max = 20, message = "El RUC debe tener al menos 11 caracteres")
    private String ruc;

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(max = 150, message = "El nombre comercial no puede exceder los 150 caracteres")
    private String nombreComercial;

    @Size(max = 200, message = "La razón social no puede exceder los 200 caracteres")
    private String razonSocial;

    @NotBlank(message = "La dirección fiscal es obligatoria")
    private String direccionFiscal;

    @Email(message = "El formato del correo de contacto no es válido")
    @Size(max = 150, message = "El correo de contacto no puede exceder los 150 caracteres")
    private String correoContacto;

    @Size(max = 20, message = "El teléfono de contacto no puede exceder los 20 caracteres")
    private String telefonoContacto;

    private BigDecimal latitud;
    private BigDecimal longitud;

    @NotBlank(message = "El nombre del encargado es obligatorio")
    @Size(max = 150, message = "El nombre del encargado no puede exceder los 150 caracteres")
    private String encargadoNombre;

    @NotBlank(message = "El apellido del encargado es obligatorio")
    @Size(max = 150, message = "El apellido del encargado no puede exceder los 150 caracteres")
    private String encargadoApellido;

    @NotBlank(message = "El DNI del encargado es obligatorio")
    @Size(min = 8, max = 20, message = "El DNI debe tener al menos 8 dígitos")
    private String encargadoDni;

    @NotBlank(message = "El teléfono del encargado es obligatorio")
    @Size(max = 20, message = "El teléfono del encargado no puede exceder los 20 caracteres")
    private String encargadoTelefono;

    @NotBlank(message = "El correo del encargado es obligatorio")
    @Email(message = "El formato del correo del encargado no es válido")
    @Size(max = 150, message = "El correo del encargado no puede exceder los 150 caracteres")
    private String encargadoCorreo;
}