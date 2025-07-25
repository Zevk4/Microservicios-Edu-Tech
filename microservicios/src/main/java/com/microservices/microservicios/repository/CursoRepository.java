package com.microservices.microservicios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservices.microservicios.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{

}
