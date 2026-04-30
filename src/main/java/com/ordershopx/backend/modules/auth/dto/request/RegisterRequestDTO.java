package com.ordershopx.backend.modules.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    // USUARIO
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String correoElectronico;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 50, message = "Debe tener entre 6 y 50 caracteres")
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9\\d{8}$", message = "Teléfono inválido (9 dígitos y empieza en 9)")
    private String telefono;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(
            regexp = "COMENSAL|RESTAURANTE|ADMINISTRADOR",
            message = "Rol inválido"
    )
    private String rol;

    // CLIENTE
    private String nombre;
    private String apellido;

    // RESTAURANTE
    private String nombreComercial;
    private String razonSocial;

    @Pattern(regexp = "^\\d{11}$", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    private String direccionFiscal;
}