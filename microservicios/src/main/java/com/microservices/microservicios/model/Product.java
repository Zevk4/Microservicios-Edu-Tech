package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Schema(description = "Producto de la tienda gaming con información detallada.")
public class Product {

    @Id
    @Schema(description = "Código único autogenerado del producto.", example = "CO001")
    private String codigo;

    @Column(nullable = false)
    @Schema(description = "Nombre del producto.", example = "PlayStation 5")
    private String nombre;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del producto.",
            example = "Consola PlayStation 5 con disco de 825GB, control DualSense y 4K gaming.")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Precio del producto.", example = "499990.00")
    private BigDecimal precio;

    @Column(nullable = false)
    @Schema(description = "Categoría principal del producto.", example = "Consolas")
    private String categoria;

    @Column(nullable = false)
    @Schema(description = "Subcategoría específica del producto.", example = "PlayStation")
    private String subcategoria;

    // Agregamos columnDefinition = "LONGTEXT"
    @Column(nullable = false, columnDefinition = "LONGTEXT") 
    @Schema(description = "URL de la imagen del producto o Base64.", example = "https://ejemplo.com/ps5.jpg")
    private String imagen;

    @Column(nullable = false)
    @Schema(description = "Marca del producto.", example = "Sony")
    private String marca;

    // Constructor sin código para creación
    public Product(String nombre, String descripcion, BigDecimal precio, String categoria,
                   String subcategoria, String imagen, String marca) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.imagen = imagen;
        this.marca = marca;
    }
}
