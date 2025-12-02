package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp; // Import for CreationTimestamp

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_items")
@Schema(description = "Item del carrito de compras de un usuario.")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del item en carrito.", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Usuario dueño del carrito.")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_codigo", nullable = false)
    @Schema(description = "Producto agregado al carrito.")
    private Product product;

    @Column(nullable = false)
    @Schema(description = "Cantidad del producto en el carrito.", example = "2")
    private Integer quantity;

    @CreationTimestamp
    @Schema(description = "Fecha y hora de creación del item.",
            example = "2025-12-01T10:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;

    // Constructor sin id
    public CartItem(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }
}
