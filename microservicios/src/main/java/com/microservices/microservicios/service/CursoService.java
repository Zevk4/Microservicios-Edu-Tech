package com.microservices.microservicios.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.repository.CursoRepository;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursRepo;

    // Guardar un Curso
    public Curso guardar(Curso cur){
        Curso c1 = new Curso(cur.getTitulo(), cur.getCategoria(), cur.getDescripcion(), cur.getInstructor(), cur.getPrice(), cur.getPopularidad());
        return cursRepo.save(c1);
    }

    // Ver Todos los Cursos
    public ArrayList<Curso> verCursos(){
        return (ArrayList<Curso>) cursRepo.findAll();
    }

    // Obtener Curso por ID
    public Optional<Curso> buscarCurso(Long id){
        return cursRepo.findById(id);
    }

    //Metodo para modificar un Curso
    public Curso actualizarCurso(Curso curso1, Long id){
          Curso cur = cursRepo.findById(id).get();
        
          cur.setTitulo(curso1.getTitulo());
          cur.setCategoria(curso1.getCategoria());
          cur.setDescripcion(curso1.getDescripcion());
          cur.setInstructor(curso1.getInstructor());
          cur.setPrice(curso1.getPrice());
          cur.setPopularidad(curso1.getPopularidad());
          cursRepo.save(cur);
          return cur;
    }

    //Metodo para eliminar un Curso por ID
    public Boolean eliminarPorId(Long id){
        try{
            cursRepo.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
