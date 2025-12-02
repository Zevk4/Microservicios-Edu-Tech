package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request para registro de nuevo usuario.")
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre completo.", example = "Juan Pérez")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Email único.", example = "juan@duocuc.cl")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
    @Schema(description = "Password (mínimo 6 caracteres).", example = "password123")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "CLIENTE|VENDEDOR|ADMIN", message = "El rol debe ser CLIENTE, VENDEDOR o ADMIN")
    @Schema(description = "Rol del usuario.", example = "CLIENTE", allowableValues = {"CLIENTE", "VENDEDOR", "ADMIN"})
    private String role;
}
