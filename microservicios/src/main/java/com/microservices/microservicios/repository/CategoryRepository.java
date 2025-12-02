package com.microservices.microservicios.repository;

import com.microservices.microservicios.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> { // <--- Clave: Long

    // Mantenemos este por si quieres evitar crear duplicados con el mismo nombre
    boolean existsByTitle(String title);
    
    // Mantenemos este por si necesitas buscar por nombre en algún buscador
    Optional<Category> findByTitle(String title);

    // Búsqueda personalizada por nombre de subcategoría
    @Query("SELECT c FROM Category c JOIN c.subcategories s WHERE s.name = :subcategoryName")
    List<Category> findBySubcategoryName(String subcategoryName);
}