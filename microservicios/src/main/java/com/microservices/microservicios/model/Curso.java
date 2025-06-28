package com.microservices.microservicios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "curso")
@Schema(description = "Información del Curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String titulo;
    @Column
    private String categoria;
    @Column
    private String descripcion;
    @Column
    private String instructor;
    @Column
    private double price;
    @Column
    private double popularidad;


    // Constructor sin id para crear un Curso
    public Curso(String titulo, String categoria, String descripcion, String instructor, double price, double popularidad){
        super();
        this.titulo = titulo;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.instructor = instructor;
        this.price = price;
        this.popularidad = popularidad;
    }

}
