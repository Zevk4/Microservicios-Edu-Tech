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
@Schema(description = "Información detallada sobre un curso ofrecido en la plataforma Edu-Tech.")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del curso.", example = "101")
    private Long id;

    @Column
    @Schema(description = "Título completo del curso.", example = "Introducción a la Programación con Python",
            maxLength = 255) 
    private String titulo;

    @Column
    @Schema(description = "Categoría a la que pertenece el curso.", example = "Programación")
    private String categoria;

    @Column
    @Schema(description = "Descripción detallada del contenido y objetivos del curso.",
            example = "Este curso cubre los fundamentos de Python, desde la sintaxis básica hasta conceptos avanzados de programación orientada a objetos.")
    private String descripcion;

    @Column
    @Schema(description = "Nombre completo del instructor que imparte el curso.", example = "Dr. Ana María Pérez")
    private String instructor;

    @Column
    @Schema(description = "Precio del curso en moneda local (CLP).", example = "85.000")
    private double price;

    @Column
    @Schema(description = "Popularidad o calificación promedio del curso (en una escala de 1 a 5).", example = "4.7")
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