package com.microservices.microservicios.controller;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
    

    @Autowired
    private UsuarioService userService;

    @GetMapping("/usuarios")
    public ArrayList<Usuario> getUsuarios(){
        return userService.getUsuarios();
    }

    @PostMapping("/registro")
    public Usuario  crearUsuario(@RequestBody Usuario user){
        return userService.crearUsuario(user);
    }

    @GetMapping("/{id}")
    public Optional<Usuario> getById(@PathVariable("id") Long id){
        return userService.getById(id);
    }

    @PutMapping(path = "{id}")
    public Usuario update(@RequestBody Usuario user, @PathVariable Long id){
        return userService.updateById(user, id);
    }

    @DeleteMapping("/eliminar{id}")
    public String eliminar(@PathVariable("id") Long id){
        boolean ok = this.userService.deleteById(id);
        if(ok){
            return "El usuario con la id " + id + " ha sido eliminado";
        }else{
            return "El usuario con la id " + id + " no existe";
        }
    }

    @PutMapping("/cambiarRol/{id}")
    public ResponseEntity<Usuario> cambiarRol(@PathVariable("id") Long id,@RequestParam("newRoleName") String nuevoRol) {
        Usuario actualizado = userService.changeRol(id, nuevoRol);
        return ResponseEntity.ok(actualizado);
    }

}
