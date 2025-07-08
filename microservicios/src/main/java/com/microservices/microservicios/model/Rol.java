package com.microservices.microservicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "rol")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Representa un rol de usuario dentro del sistema, definiendo sus permisos o categoría.")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del rol.", example = "1")
    private Long id;

    @Column(unique = true)
    @Schema(description = "Nombre único del rol.", example = "INSTRUCTOR", maxLength = 50)
    private String nombre;

    //Constructor solo con el nombre
    public Rol(String nombre){
        this.nombre = nombre;
    }
}