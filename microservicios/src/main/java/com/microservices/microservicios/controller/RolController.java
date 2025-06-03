package com.microservices.microservicios.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.service.RolService;

@RestController
@RequestMapping("/rol")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping("/verrol")
    public ArrayList<Rol> verCursos(){
        return rolService.verRoles();
    }

    @PostMapping("/saverol")
    public Rol crearCurso(@RequestBody Rol rol){
        return rolService.guardar(rol);
    }

    @GetMapping("/{id}")
    public Optional<Rol> buscarId(@PathVariable("id") Long id){
        return rolService.buscarRoles(id);
    }

    @PutMapping(path = "{id}")
    public Rol actualizar(@RequestBody Rol rol, @PathVariable Long id){
        return rolService.actualizarRol(rol, id);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable("id") Long id){
        boolean ok = this.rolService.eliminarRolId(id);
        if(ok){
            return "El Rol con la id " + id + " ha sido eliminado";
        }else{
            return "El Rol con la id " + id + " no existe";
        }
    }

}
