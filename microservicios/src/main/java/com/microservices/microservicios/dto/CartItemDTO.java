package com.microservices.microservicios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de item de carrito para respuestas API.")
public class CartItemDTO {

    @Schema(description = "Identificador único del item en carrito.", example = "1")
    private Long id;

    @Schema(description = "Información del producto en el carrito.")
    private ProductDTO product;

    @Schema(description = "Cantidad del producto en el carrito.", example = "2")
    private Integer quantity;

    @Schema(description = "Fecha y hora de creación del item.",
            example = "2025-12-01T10:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;

    @Schema(description = "Subtotal del item (precio * cantidad).", example = "999980.00")
    private BigDecimal subtotal;
}
