package com.microservices.microservicios.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request para agregar/actualizar item en carrito.")
public class CartItemRequest {

    @NotBlank(message = "El código del producto es obligatorio")
    @Schema(description = "Código del producto.", example = "CO001")
    private String productCodigo;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 99, message = "La cantidad máxima es 99")
    @Schema(description = "Cantidad del producto.", example = "2")
    private Integer quantity;
}
