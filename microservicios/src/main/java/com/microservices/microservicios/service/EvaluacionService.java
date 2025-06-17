package com.microservices.microservicios.service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.repository.EvaluacionRepository;

@Service
public class EvaluacionService {
    @Autowired
    private EvaluacionRepository evaRepo;

    // Guardar una Evaluacion
    public Evaluacion crearEvaluacion(Evaluacion eva){
        Evaluacion e1 = new Evaluacion(eva.getNombre(), eva.getDescripcion(), eva.getTipo(), eva.getFecha_inicio(), eva.getFecha_termino(), eva.getDuracion(), eva.getCalificacionMaxima(), eva.getEstado(), eva.getCurso());
        return evaRepo.save(e1);
    }

    // Ver Todas las Evaluaciones
    public ArrayList<Evaluacion> verEvaluacion(){
        return (ArrayList<Evaluacion>) evaRepo.findAll();
    }

    // Obtener Evaluacion por ID
    public Optional<Evaluacion> buscarEvaluacion(Long id){
        return evaRepo.findById(id);
    }

    //Metodo para modificar una Evaluacion
    public Evaluacion actualizarEvaluacion(Evaluacion eva1, Long id){
        Evaluacion eva = evaRepo.findById(id).get();
        
          eva.setNombre(eva1.getNombre());
          eva.setDescripcion((eva1.getDescripcion()));
          eva.setTipo(eva1.getTipo());
          eva.setFecha_inicio(eva1.getFecha_inicio());
          eva.setFecha_termino(eva1.getFecha_termino());
          eva.setDuracion(eva1.getDuracion());
          eva.setCalificacionMaxima(eva1.getCalificacionMaxima());
          eva.setEstado(eva1.getEstado());
          eva.setCurso(eva1.getCurso());
          evaRepo.save(eva);
          return eva;
    }

    //Metodo para eliminar una Evaluacion por ID
    public Boolean eliminarPorId(Long id){
        try{
            evaRepo.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

}
