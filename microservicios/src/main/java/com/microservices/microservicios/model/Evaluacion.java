package com.microservices.microservicios.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evaluacion")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nombre;
    @Column
    private String descripcion;
    @Column
    private String tipo;
    @Column
    private LocalDateTime fecha_inicio;
    @Column
    private LocalDateTime fecha_termino;
    @Column
    private Integer duracion;
    @Column 
    private double calificacionMaxima;
    @Column 
    private String estado;
    @ManyToOne
    @JoinColumn(name = "id")
    private Curso curso;
    
    public Evaluacion(String nombre, String descripcion, String tipo, LocalDateTime fecha_inicio, LocalDateTime fecha_termino, Integer duracion, double calificacionMaxima, String estado, Curso curso){
        super();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fecha_inicio = fecha_inicio;
        this.fecha_termino = fecha_termino;
        this.duracion = duracion;
        this.calificacionMaxima = calificacionMaxima;
        this.estado = estado;
        this.curso = curso;
    }

}
