package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request para login de usuario.")
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Schema(description = "Email del usuario.", example = "juan@duocuc.cl")
    private String email;

    @NotBlank(message = "El password es obligatorio")
    @Schema(description = "Password del usuario.", example = "password123")
    private String password;
}
