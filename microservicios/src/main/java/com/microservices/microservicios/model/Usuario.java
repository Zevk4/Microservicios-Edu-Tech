package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa la información de un usuario registrado en la plataforma Edu-Tech.")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario.", example = "1")
    private Long id;

    @Column
    @Schema(description = "Nombre completo del usuario.", example = "Juan Pérez")
    private String nombre;

    @Column(unique = true)
    @Schema(description = "Dirección de correo electrónico única del usuario.", example = "juan.perez@example.com")
    private String email;

    @Column
    @Schema(description = "Contraseña del usuario.", example = "hashed_password123", accessMode = Schema.AccessMode.WRITE_ONLY)
    // Usaremos WRITE_ONLY para que no se muestre en respuestas GET, pero sí se pueda enviar en PUT/POST
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id") // Nombre de la columna FK en la tabla usuario
    @Schema(description = "El rol asignado al usuario (ej. ADMIN, ESTUDIANTE, INSTRUCTOR)",
            implementation = Rol.class)
    private Rol rol;

    // Constructor sin id para crear un usuario
    public Usuario(String nombre, String email, String password, Rol rol){
        super();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }
}