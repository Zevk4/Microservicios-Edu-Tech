package com.microservices.microservicios.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository userRepo;
    @Autowired
    private RolRepository rolRepo;


    //Metodo para ver los usuarios en el repositorio
    public ArrayList<Usuario> getUsuarios(){
        return (ArrayList<Usuario>) userRepo.findAll();
    }

    //Metodo para guardar usuarios en el repositorio
    public Usuario crearUsuario(Usuario user) {
        Optional<Rol> rolOptional = rolRepo.findByNombre("Estudiante");

        if (rolOptional.isEmpty()) {
            throw new RuntimeException("Rol 'Estudiante' no encontrado en la base de datos.");
        }

        Rol xrol = rolOptional.get();

        Usuario x = new Usuario(user.getNombre(), user.getEmail(), user.getPassword(), xrol);
        return userRepo.save(x);

    }

    //Metodo para obtener un usuario por su ID
    public Optional<Usuario> getById(Long id){
        return userRepo.findById(id);
    }

    //Metodo para modificar un Usuario
    public Usuario updateById(Usuario user, Long id){
          Usuario var = userRepo.findById(id).get();
        
          var.setNombre(user.getNombre());
          var.setEmail(user.getEmail());
          var.setPassword(user.getPassword());
          var.setRol(user.getRol());
          userRepo.save(var);
          return var;
    }

    //Metodo para eliminar un Usuario por ID
    public Boolean deleteById(Long id){
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }else {
            // Si no existe, consideramos que no se "pudo" eliminar (porque no estaba ahí)
            return false;
        }
    }

    //Metodo para Cambiar Rol del Usuario
    public Usuario changeRol(Long userId, String newRoleName) {
        Optional<Usuario> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario con ID " + userId + " no encontrado.");
        }
        Usuario existingUser = userOptional.get();
        Optional<Rol> rolOptional = rolRepo.findByNombre(newRoleName); 

        if (rolOptional.isEmpty()) {
            throw new RuntimeException("Rol '" + newRoleName + "' no encontrado en la base de datos.");
        }
        Rol newRol = rolOptional.get();
        existingUser.setRol(newRol);
        return userRepo.save(existingUser);
    }

}
