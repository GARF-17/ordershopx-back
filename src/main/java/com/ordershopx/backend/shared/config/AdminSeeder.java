package com.ordershopx.backend.shared.config;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.shared.enums.RolGlobal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@ordershopx.com";
        String adminDni = "00000000";


        if (!usuarioRepository.existsByCorreoElectronico(adminEmail)
                && !usuarioRepository.existsByDni(adminDni)) {
            log.info("Iniciando la creación del Usuario Administrador principal...");

            Usuario admin = Usuario.builder()
                    .correoElectronico(adminEmail)
                    .claveHash(passwordEncoder.encode("admin1234"))
                    .rol(RolGlobal.ADMINISTRADOR)
                    .dni(adminDni)
                    .tipoDocumento("DNI")
                    .telefono("999999999")
                    .estaActivo(true)
                    .build();

            usuarioRepository.save(admin);

            log.info(" ¡Super Administrador creado exitosamente en la Base de Datos!");
            log.info(" Correo: {}", adminEmail);
            log.info(" Contraseña: admin1234");
        } else {
            log.info("El usuario Administrador ya existe en la base de datos (por correo o dni). Saltando creación.");
        }
    }
}