package com.microservices.microservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación del sistema.")
public class AuthResponse {

    @Schema(description = "Indica si la operación fue exitosa.", example = "true")
    private boolean success;

    @Schema(description = "Mensaje de respuesta.", example = "Login exitoso")
    private String message;

    @Schema(description = "Información del usuario autenticado.")
    private UserDTO user;

    @Schema(description = "Token JWT para autenticación.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    // Constructor para mensajes de error
    public AuthResponse(String message) {
        this.success = false;
        this.message = message;
        this.user = null;
        this.token = null;
    }

    // Constructor para login/registro exitoso
    public AuthResponse(UserDTO user, String token) {
        this.success = true;
        this.message = "Operación exitosa";
        this.user = user;
        this.token = token;
    }
}
