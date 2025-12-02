package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request para crear o actualizar subcategoría.")
public class SubcategoryRequest {

    // --- ¡ESTE ES EL CAMPO CLAVE! ---
    @Schema(description = "ID de la subcategoría. Envíalo para ACTUALIZAR, déjalo nulo para CREAR una nueva.", example = "15")
    private Long id; 

    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    @Schema(description = "Nombre de la subcategoría.", example = "Notebook Gamer")
    private String name;

    @NotBlank(message = "El enlace de la subcategoría es obligatorio")
    @Schema(description = "Enlace de la subcategoría.", example = "/category?cat=Computacion&sub=Notebook%20Gamer")
    private String link;
}