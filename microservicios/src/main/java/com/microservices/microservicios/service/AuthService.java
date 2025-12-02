package com.microservices.microservicios.service;  
  
import com.microservices.microservicios.dto.AuthResponse;  
import com.microservices.microservicios.dto.UserDTO;  
import com.microservices.microservicios.dto.requests.LoginRequest;  
import com.microservices.microservicios.dto.requests.RegisterRequest;  
import com.microservices.microservicios.model.User;  
import com.microservices.microservicios.enums.UserRole;  
import com.microservices.microservicios.repository.UserRepository;  
import com.microservices.microservicios.config.JwtTokenProvider;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.security.crypto.password.PasswordEncoder;  
import org.springframework.stereotype.Service;  
import org.springframework.transaction.annotation.Transactional;  
  
@Service  
@Transactional  
public class AuthService {  
      
    @Autowired  
    private UserRepository userRepository;  
      
    @Autowired  
    private PasswordEncoder passwordEncoder;  
      
    @Autowired  
    private JwtTokenProvider tokenProvider;  
      
    public AuthResponse login(LoginRequest request) {  
        User user = userRepository.findByEmail(request.getEmail())  
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));  
              
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {  
            return new AuthResponse(false, "Credenciales inválidas", null, null);  
        }  
          
        String token = tokenProvider.generateToken(user.getEmail());  
        UserDTO userDTO = new UserDTO(user);  
          
        return new AuthResponse(true, "Login exitoso", userDTO, token);  
    }  
      
    public AuthResponse register(RegisterRequest request) {  
        if (userRepository.existsByEmail(request.getEmail())) {  
            return new AuthResponse(false, "El email ya está registrado", null, null);  
        }  
          
        User user = new User();  
        user.setNombre(request.getNombre());  
        user.setEmail(request.getEmail());  
        user.setPassword(passwordEncoder.encode(request.getPassword()));  
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));  
          
        User savedUser = userRepository.save(user);  
        String token = tokenProvider.generateToken(savedUser.getEmail());  
        UserDTO userDTO = new UserDTO(savedUser);  
          
        return new AuthResponse(true, "Registro exitoso", userDTO, token);  
    }  
}