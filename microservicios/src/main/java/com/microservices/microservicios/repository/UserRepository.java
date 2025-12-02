package com.microservices.microservicios.repository;  
  
import com.microservices.microservicios.model.User;  
import com.microservices.microservicios.enums.UserRole;  
import org.springframework.data.jpa.repository.JpaRepository;  
import org.springframework.stereotype.Repository;  
  
import java.util.Optional;  
  
@Repository  
public interface UserRepository extends JpaRepository<User, Long> {  
      
    Optional<User> findByEmail(String email);  
      
    boolean existsByEmail(String email);  
      
    Optional<User> findByRole(UserRole role);  
}