package com.microservices.microservicios.service;

import com.microservices.microservicios.dto.UserDTO;
import com.microservices.microservicios.dto.requests.RegisterRequest;
import com.microservices.microservicios.model.User;
import com.microservices.microservicios.enums.UserRole;
import com.microservices.microservicios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    public UserDTO createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        User user = new User();
        user.setNombre(request.getNombre());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // En lugar de leer el rol del request, lo forzamos nosotros.
        // Asumiendo que EL Enum tiene un valor llamado CLIENTE:
        user.setRole(UserRole.CLIENTE);
        // Si en el JSON envían "ADMIN",
        // EL código lo ignorará y siempre creará un CLIENTE.
        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public UserDTO updateUser(Long id, RegisterRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setNombre(request.getNombre());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }
}