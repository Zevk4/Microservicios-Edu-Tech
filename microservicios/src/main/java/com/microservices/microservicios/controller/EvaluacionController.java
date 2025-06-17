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

import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.service.EvaluacionService;

@RestController
@RequestMapping("/evaluacion")
public class EvaluacionController {
    @Autowired
    private EvaluacionService evaService;

    @GetMapping("/evaluaciones")
    public ArrayList<Evaluacion> getEvaluaciones(){
        return evaService.verEvaluacion();
    }

    @PostMapping("/crearEvaluacion")
    public Evaluacion crearEvaluacion(@RequestBody Evaluacion eva){
        return evaService.crearEvaluacion(eva);
    }

    @GetMapping("/{id}")
    public Optional<Evaluacion> buscarEvaluacion(@PathVariable("id") Long id){
        return evaService.buscarEvaluacion(id);
    }

    @PutMapping(path = "{id}")
    public Evaluacion atualizarEvaluacion(@RequestBody Evaluacion eva, @PathVariable Long id){
        return evaService.actualizarEvaluacion(eva, id);
    }

    @DeleteMapping("/eliminar{id}")
    public String eliminarEvaluacion(@PathVariable("id") Long id){
        boolean ok = this.evaService.eliminarPorId(id);
        if(ok){
            return "La Evaluacion con la id " + id + " ha sido eliminada";
        }else{
            return "La Evaluacion con la id " + id + " no existe";
        }
    }

}
