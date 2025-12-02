package com.microservices.microservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de producto para respuestas API.")
public class ProductDTO {

    @Schema(description = "Código del producto.", example = "CO001")
    private String codigo;

    @Schema(description = "Nombre del producto.", example = "PlayStation 5")
    private String nombre;

    @Schema(description = "Descripción del producto.", example = "Consola de última generación...")
    private String descripcion;

    @Schema(description = "Precio del producto.", example = "499990.00")
    private BigDecimal precio;

    @Schema(description = "Categoría del producto.", example = "Consolas")
    private String categoria;

    @Schema(description = "Subcategoría del producto.", example = "PlayStation")
    private String subcategoria;

    @Schema(description = "URL de la imagen.", example = "https://ejemplo.com/ps5.jpg")
    private String imagen;

    @Schema(description = "Marca del producto.", example = "Sony")
    private String marca;

    public ProductDTO(com.microservices.microservicios.model.Product product) {
        this.codigo = product.getCodigo();
        this.nombre = product.getNombre();
        this.descripcion = product.getDescripcion();
        this.precio = product.getPrecio();
        this.categoria = product.getCategoria();
        this.subcategoria = product.getSubcategoria();
        this.imagen = product.getImagen();
        this.marca = product.getMarca();
    }
}

