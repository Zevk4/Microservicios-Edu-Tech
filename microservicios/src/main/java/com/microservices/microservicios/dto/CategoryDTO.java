package com.microservices.microservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de categoría para respuestas API.")
public class CategoryDTO {

    // --- AGREGADO: El campo ID ---
    @Schema(description = "Identificador único de la categoría.", example = "1")
    private Long id; 

    @Schema(description = "Título de la categoría.", example = "Consolas")
    private String title;

    @Schema(description = "URL link de la categoría.", example = "/category?cat=Consolas")
    private String link;

    @Schema(description = "Lista de subcategorías.")
    private List<SubCategoryDTO> subcategories;
}