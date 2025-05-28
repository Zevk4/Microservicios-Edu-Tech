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
        try{
            rolRepo.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

}
