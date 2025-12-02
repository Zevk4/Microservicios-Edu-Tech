package com.microservices.microservicios.dto;

import com.microservices.microservicios.model.User; // Import User entity
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de usuario para respuestas API (sin password).")
public class UserDTO {

    @Schema(description = "ID del usuario.", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario.", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Email del usuario.", example = "juan@duocuc.cl")
    private String email;

    @Schema(description = "Rol del usuario.", example = "cliente")
    private String role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.nombre = user.getNombre();
        this.email = user.getEmail();
        this.role = user.getRole().name(); // Convert UserRole enum to String
    }
}