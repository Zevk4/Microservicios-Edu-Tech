package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subcategories")
@Schema(description = "Subcategoría específica dentro de una categoría.")
// Excluir category del toString y equals
@ToString(exclude = "category")
@EqualsAndHashCode(exclude = "category")
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la subcategoría.", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre de la subcategoría.", example = "PlayStation")
    private String name;

    @Column(nullable = false)
    @Schema(description = "URL link de la subcategoría.", example = "/category?cat=Consolas&sub=PlayStation")
    private String link;

    // --- CORRECCIÓN AQUÍ ---
    @ManyToOne
    // Cambiamos 'category_title' por 'category_id' y quitamos 'referencedColumnName'
    // Hibernate automáticamente sabrá que debe buscar la Primary Key (id)
    @JoinColumn(name = "category_id", nullable = false) 
    @Schema(description = "Categoría padre a la que pertenece.")
    private Category category;

    // Constructor sin id
    public Subcategory(String name, String link, Category category) {
        this.name = name;
        this.link = link;
        this.category = category;
    }
}