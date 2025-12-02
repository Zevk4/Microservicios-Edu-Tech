package com.microservices.microservicios.repository;  
  
import com.microservices.microservicios.model.Product;  
import org.springframework.data.jpa.repository.JpaRepository;  
import org.springframework.data.jpa.repository.Query;  
import org.springframework.stereotype.Repository;  
  
import java.util.List;  
  
@Repository  
public interface ProductRepository extends JpaRepository<Product, String> {  
      
    List<Product> findByCategoria(String categoria);  
      
    List<Product> findBySubcategoria(String subcategoria);  
      
    List<Product> findByMarca(String marca);  
      
    List<Product> findByNombreContainingIgnoreCase(String nombre);  
      
    long countBySubcategoriaStartingWith(String subcategoria); // Added comment to force recompile

    @Query("SELECT p.codigo FROM Product p WHERE p.subcategoria = ?1 ORDER BY p.codigo DESC")  
    List<String> findLastCodigoBySubcategoria(String subcategoria);  
      
    @Query("SELECT p FROM Product p WHERE " +  
           "(:marca IS NULL OR p.marca = :marca) AND " +  
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +  
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")  
    List<Product> findWithFilters(String marca, String categoria, String nombre);  
}