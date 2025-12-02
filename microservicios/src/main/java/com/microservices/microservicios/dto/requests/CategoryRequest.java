package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Request para crear o actualizar categoría.")
public class CategoryRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Schema(description = "Título de la categoría.", example = "Consolas")
    private String title;

    @NotBlank(message = "El enlace es obligatorio")
    @Pattern(regexp = "^/category\\?cat=.+$", message = "El enlace debe seguir el formato /category?cat=nombre")
    @Schema(description = "URL de la categoría.", example = "/category?cat=Consolas")
    private String link;

    @Valid
    @Schema(description = "Lista de subcategorías.")
    private List<SubcategoryRequest> subcategories;
}
