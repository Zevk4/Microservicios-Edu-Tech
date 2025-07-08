package com.microservices.microservicios.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Información detallada sobre una evaluación o actividad de un curso específico.")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la evaluación.", example = "201")
    private Long id;

    @Column
    @Schema(description = "Nombre de la evaluación.", example = "Examen Parcial de Python")
    private String nombre;

    @Column
    @Schema(description = "Descripción detallada de la evaluación, incluyendo instrucciones o temas cubiertos.",
            example = "Examen que cubre los temas de variables, estructuras de control y funciones en Python.")
    private String descripcion;

    @Column
    @Schema(description = "Tipo de evaluación (ej. 'Examen', 'Tarea', 'Proyecto', 'Cuestionario').", example = "Examen")
    private String tipo;

    @Column
    @Schema(description = "Fecha y hora en que la evaluación estará disponible para los estudiantes.",
            example = "2025-07-15T09:00:00", type = "string", format = "date-time")
    private LocalDateTime fecha_inicio;

    @Column
    @Schema(description = "Fecha y hora límite para completar la evaluación.",
            example = "2025-07-15T11:00:00", type = "string", format = "date-time")
    private LocalDateTime fecha_termino;

    @Column
    @Schema(description = "Duración estimada de la evaluación en minutos.", example = "120")
    private Integer duracion;

    @Column
    @Schema(description = "Calificación máxima posible para esta evaluación.", example = "100.0")
    private double calificacionMaxima;

    @Column
    @Schema(description = "Estado actual de la evaluación (ej. 'Pendiente', 'Activo', 'Finalizado', 'Calificado').", example = "Activo")
    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    @Schema(description = "El curso al que pertenece esta evaluación.")
    private Curso curso; 

    // Constructor sin id para crear una Evaluacion
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