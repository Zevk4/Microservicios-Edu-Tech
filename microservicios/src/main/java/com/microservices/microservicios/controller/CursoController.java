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

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.service.CursoService;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoService cursService;

    @GetMapping("/vercursos")
    public ArrayList<Curso> verCursos(){
        return cursService.verCursos();
    }

    @PostMapping("/ingresarCurso")
    public Curso crearCurso(@RequestBody Curso curs){
        return cursService.guardar(curs);
    }

    @GetMapping("/{id}")
    public Optional<Curso> buscarId(@PathVariable("id") Long id){
        return cursService.buscarCurso(id);
    }

    @PutMapping(path = "{id}")
    public Curso actualizar(@RequestBody Curso curs, @PathVariable Long id){
        return cursService.actualizarCurso(curs, id);
    }

    @DeleteMapping("/eliminar{id}")
    public String eliminar(@PathVariable("id") Long id){
        boolean ok = this.cursService.eliminarPorId(id);
        if(ok){
            return "El Curso con la id " + id + " ha sido eliminado";
        }else{
            return "El Curso con la id " + id + " no existe";
        }
    }

}
