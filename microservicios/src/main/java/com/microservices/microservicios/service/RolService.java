package com.microservices.microservicios.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.repository.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepo;

    // Guardar un Curso
    public Rol guardar(Rol rol){
        Rol rol1 = new Rol(rol.getNombre());
        return rolRepo.save(rol1);
    }

    // Ver Todos los Cursos
    public ArrayList<Rol> verRoles(){
        return (ArrayList<Rol>) rolRepo.findAll();
    }

    // Obtener Curso por ID
    public Optional<Rol> buscarRoles(Long id){
        return rolRepo.findById(id);
    }

    //Metodo para modificar un Curso
    public Rol actualizarRol(Rol rol, Long id){
          Rol rol1 = rolRepo.findById(id).get();
        
          rol1.setNombre(rol.getNombre());
          rolRepo.save(rol1);
          return rol1;
    }

    //Metodo para eliminar un Curso por ID
    public Boolean eliminarRolId(Long id){
        //Verificar antes de eliminar 
        if (rolRepo.existsById(id)) {
            rolRepo.deleteById(id);
            return true;
        } else {
            // Si no existe, consideramos que no se "pudo" eliminar (porque no estaba ahí)
            return false;
        }
    }

}
