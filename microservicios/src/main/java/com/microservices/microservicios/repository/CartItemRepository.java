package com.microservices.microservicios.repository;  
  
import com.microservices.microservicios.model.CartItem;  
import com.microservices.microservicios.model.User;  
import com.microservices.microservicios.model.Product;  
import org.springframework.data.jpa.repository.JpaRepository;  
import org.springframework.data.jpa.repository.Query;  
import org.springframework.stereotype.Repository;  
  
import java.util.List;  
import java.util.Optional;  
  
@Repository  
public interface CartItemRepository extends JpaRepository<CartItem, Long> {  
      
    List<CartItem> findByUserOrderByCreatedAtDesc(User user);  
      
    Optional<CartItem> findByUserAndProduct(User user, Product product);  
      
    Optional<CartItem> findByIdAndUser(Long id, User user);  
      
    List<CartItem> findByUser(User user);  
      
    void deleteByUser(User user);  
      
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user = :user")  
    Long countByUser(User user);  
      
    @Query("SELECT SUM(ci.quantity * ci.product.precio) FROM CartItem ci WHERE ci.user = :user")  
    Double getTotalByUser(User user);  
}