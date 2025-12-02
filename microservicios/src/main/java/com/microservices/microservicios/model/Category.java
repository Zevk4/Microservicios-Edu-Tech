package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories")
@Schema(description = "Categoría de productos con sus subcategorías.")
@ToString(exclude = "subcategories") 
@EqualsAndHashCode(exclude = "subcategories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la categoría.", example = "1")
    private Long id; 
    
    @Column(unique = true, nullable = false)
    @Schema(description = "Título único de la categoría.", example = "Consolas")
    private String title;

    @Column(nullable = false, unique = true)
    @Schema(description = "URL link de la categoría.", example = "/category?cat=Consolas")
    private String link;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Schema(description = "Lista de subcategorías asociadas.")
    private List<Subcategory> subcategories = new ArrayList<>();


}
