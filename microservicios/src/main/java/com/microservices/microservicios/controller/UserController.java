package com.microservices.microservicios.controller;  
  
import com.microservices.microservicios.dto.UserDTO;  
import com.microservices.microservicios.dto.requests.RegisterRequest;  
import com.microservices.microservicios.service.UserService;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.ResponseEntity;  
import org.springframework.web.bind.annotation.*;  
  
import jakarta.validation.Valid;  
import java.util.List;  
  
@RestController  
@RequestMapping("/api/users")  
@CrossOrigin(origins = "*")  
public class UserController {  
      
    @Autowired  
    private UserService userService;  
      
    @GetMapping  
    public ResponseEntity<List<UserDTO>> getAllUsers() {  
        return ResponseEntity.ok(userService.getAllUsers());  
    }  
      
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody RegisterRequest request) {  
        return ResponseEntity.ok(userService.createUser(request));  
    }  
      
    @PutMapping("/{id}")  
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,   
                                             @Valid @RequestBody RegisterRequest request) {  
        return ResponseEntity.ok(userService.updateUser(id, request));  
    }  
      
    @DeleteMapping("/{id}")  
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {  
        userService.deleteUser(id);  
        return ResponseEntity.noContent().build();  
    }  
}