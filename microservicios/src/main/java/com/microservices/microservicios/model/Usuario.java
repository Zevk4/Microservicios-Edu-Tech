package com.microservices.microservicios.model;

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
@Table(name = "usuario")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información de Usuario")
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String nombre;
    @Column(unique = true)
    private String email;
    @Column
    private String password;
    @ManyToOne  // Relación muchos-a-uno con Rol
    @JoinColumn(name = "rol_id")  // Nombre de la columna FK en la tabla usuario
    private Rol rol;

    // Constructor sin id para crear un usuario
    public Usuario(String nombre, String email, String password, Rol rol){
        super();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

}
