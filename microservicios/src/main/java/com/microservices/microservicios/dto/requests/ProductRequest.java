package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

import org.hibernate.validator.constraints.URL;

@Data
@Schema(description = "Request para crear o actualizar producto.")
public class ProductRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    @Schema(description = "Nombre del producto.", example = "PlayStation 5")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 2000, message = "La descripción debe tener entre 10 y 2000 caracteres")
    @Schema(description = "Descripción detallada del producto.", example = "Consola de última generación con 4K gaming...")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "1.0", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    @Schema(description = "Precio del producto.", example = "499990.00")
    private BigDecimal precio;

    @NotBlank(message = "La categoría es obligatoria")
    @Schema(description = "Categoría del producto.", example = "Consolas")
    private String categoria;

    @NotBlank(message = "La subcategoría es obligatoria")
    @Schema(description = "Subcategoría del producto.", example = "PlayStation")
    private String subcategoria;

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @URL(message = "La URL de la imagen debe ser válida")
    @Schema(description = "URL de la imagen del producto.", example = "https://ejemplo.com/ps5.jpg")
    private String imagen;

    @NotBlank(message = "La marca es obligatoria")
    @Size(min = 2, max = 50, message = "La marca debe tener entre 2 y 50 caracteres")
    @Schema(description = "Marca del producto.", example = "Sony")
    private String marca;
}
