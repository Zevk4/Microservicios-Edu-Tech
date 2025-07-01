package com.microservices.microservicios.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.service.RolService;

// Importaciones de OpenAPI (Swagger)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/rol")
@Tag(name = "Roles", description = "API para la gestión de roles de usuario")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(summary = "Obtener todos los roles", description = "Retorna una lista de todos los roles disponibles en el sistema, con enlaces HATEOAS para cada rol y la colección.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionModel.class)))
    @GetMapping("/roles") 
    public CollectionModel<EntityModel<Rol>> verRoles(){
        List<EntityModel<Rol>> roles = rolService.verRoles().stream()
            .map(rol -> EntityModel.of(rol,
                linkTo(methodOn(RolController.class).buscarRolPorId(rol.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).verRoles()).withRel("all_roles")))
            .collect(Collectors.toList());

        return CollectionModel.of(roles, linkTo(methodOn(RolController.class).verRoles()).withSelfRel());
    }

    @Operation(summary = "Crear un nuevo rol", description = "Crea un nuevo rol y lo guarda en la base de datos, retornando el rol creado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del rol incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PostMapping("/roles") 
    public ResponseEntity<EntityModel<Rol>> crearRol(
            @RequestBody Rol rol){
        Rol newRol = rolService.guardar(rol);
        EntityModel<Rol> entityModel = EntityModel.of(newRol,
            linkTo(methodOn(RolController.class).buscarRolPorId(newRol.getId())).withSelfRel(),
            linkTo(methodOn(RolController.class).verRoles()).withRel("all_roles"),
            linkTo(methodOn(RolController.class).actualizar(null, newRol.getId())).withRel("update_role"),
            linkTo(methodOn(RolController.class).eliminar(newRol.getId())).withRel("delete_role"));

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar rol por ID", description = "Retorna un rol específico por su ID, con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Rol encontrado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                 content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> buscarRolPorId(
            @Parameter(description = "ID del rol a buscar", required = true)
            @PathVariable("id") Long id){
        Optional<Rol> rol = rolService.buscarRoles(id);

        return rol.map(r -> {
            EntityModel<Rol> entityModel = EntityModel.of(r,
                linkTo(methodOn(RolController.class).buscarRolPorId(r.getId())).withSelfRel(),
                linkTo(methodOn(RolController.class).verRoles()).withRel("all_roles"),
                linkTo(methodOn(RolController.class).actualizar(null, r.getId())).withRel("update_role"),
                linkTo(methodOn(RolController.class).eliminar(r.getId())).withRel("delete_role"));
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar un rol por ID", description = "Actualiza la información de un rol existente, retornando el rol actualizado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Rol actualizado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                 content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del rol incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PutMapping("/{id}") 
    public ResponseEntity<EntityModel<Rol>> actualizar(
            @RequestBody Rol rol,
            @Parameter(description = "ID del rol a actualizar", required = true)
            @PathVariable Long id){
        Rol updatedRol = rolService.actualizarRol(rol, id);
        if (updatedRol == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<Rol> entityModel = EntityModel.of(updatedRol,
            linkTo(methodOn(RolController.class).buscarRolPorId(updatedRol.getId())).withSelfRel(),
            linkTo(methodOn(RolController.class).verRoles()).withRel("all_roles"),
            linkTo(methodOn(RolController.class).crearRol(null)).withRel("create_role"),
            linkTo(methodOn(RolController.class).eliminar(updatedRol.getId())).withRel("delete_role"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un rol por ID", description = "Elimina un rol de la base de datos por su ID, retornando un mensaje de éxito o fracaso.")
    @ApiResponse(responseCode = "200", description = "Rol eliminado exitosamente",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "404", description = "Rol no encontrado para eliminar",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @DeleteMapping("/{id}") 
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del rol a eliminar", required = true)
            @PathVariable("id") Long id){
        boolean ok = this.rolService.eliminarRolId(id);
        if(ok){
            return new ResponseEntity<>("El Rol con la id " + id + " ha sido eliminado", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("El Rol con la id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

}