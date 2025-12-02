package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.microservices.microservicios.enums.UserRole;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Schema(description = "Usuario del sistema de tienda gaming con roles específicos.")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario.", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario.", example = "Juan Pérez")
    private String nombre;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email único del usuario.", example = "juan@duocuc.cl")
    private String email;

    @Column(nullable = false)
    @Schema(description = "Password del usuario (encriptado).", example = "password123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Rol del usuario en el sistema.", example = "CLIENTE")
    private UserRole role;

    // Constructor sin id para registro
    public User(String nombre, String email, String password, UserRole role) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}


