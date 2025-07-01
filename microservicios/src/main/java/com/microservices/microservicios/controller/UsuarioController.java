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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.service.UsuarioService;

// Importaciones de OpenAPI (Swagger)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del sistema")
public class UsuarioController {

    @Autowired
    private UsuarioService userService;

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados en el sistema, con enlaces HATEOAS para cada usuario y la colección.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionModel.class)))
    @GetMapping("/usuarios")
    public CollectionModel<EntityModel<Usuario>> getUsuarios(){
        List<EntityModel<Usuario>> usuarios = userService.getUsuarios().stream()
            .map(user -> EntityModel.of(user,
                linkTo(methodOn(UsuarioController.class).getById(user.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getUsuarios()).withRel("all_users")))
            .collect(Collectors.toList());

        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioController.class).getUsuarios()).withSelfRel());
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario y lo guarda en la base de datos, retornando el usuario creado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del usuario incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PostMapping("/crearUsuario")
    public ResponseEntity<EntityModel<Usuario>> crearUsuario(
            @RequestBody Usuario user){
        Usuario newUser = userService.crearUsuario(user);
        EntityModel<Usuario> entityModel = EntityModel.of(newUser,
            linkTo(methodOn(UsuarioController.class).getById(newUser.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getUsuarios()).withRel("all_users"),
            linkTo(methodOn(UsuarioController.class).update(null, newUser.getId())).withRel("update_user"),
            linkTo(methodOn(UsuarioController.class).eliminar(newUser.getId())).withRel("delete_user"),
            linkTo(methodOn(UsuarioController.class).cambiarRol(newUser.getId(), null)).withRel("change_role"));

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar usuario por ID", description = "Retorna un usuario específico por su ID, con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                 content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> getById(
            @Parameter(description = "ID del usuario a buscar", required = true)
            @PathVariable("id") Long id){
        Optional<Usuario> usuario = userService.getById(id);

        return usuario.map(user -> {
            EntityModel<Usuario> entityModel = EntityModel.of(user,
                linkTo(methodOn(UsuarioController.class).getById(user.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getUsuarios()).withRel("all_users"),
                linkTo(methodOn(UsuarioController.class).update(null, user.getId())).withRel("update_user"),
                linkTo(methodOn(UsuarioController.class).eliminar(user.getId())).withRel("delete_user"),
                linkTo(methodOn(UsuarioController.class).cambiarRol(user.getId(), null)).withRel("change_role"));
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar un usuario por ID", description = "Actualiza la información de un usuario existente, retornando el usuario actualizado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado para actualizar",
                 content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del usuario incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PutMapping(path = "{id}")
    public ResponseEntity<EntityModel<Usuario>> update(
            @RequestBody Usuario user,
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable Long id){
        Usuario updatedUser = userService.updateById(user, id);
        if (updatedUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<Usuario> entityModel = EntityModel.of(updatedUser,
            linkTo(methodOn(UsuarioController.class).getById(updatedUser.getId())).withSelfRel(),
            linkTo(methodOn(UsuarioController.class).getUsuarios()).withRel("all_users"),
            linkTo(methodOn(UsuarioController.class).crearUsuario(null)).withRel("create_user"),
            linkTo(methodOn(UsuarioController.class).eliminar(updatedUser.getId())).withRel("delete_user"),
            linkTo(methodOn(UsuarioController.class).cambiarRol(updatedUser.getId(), null)).withRel("change_role"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un usuario por ID", description = "Elimina un usuario de la base de datos por su ID, retornando un mensaje de éxito o fracaso.")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado para eliminar",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @DeleteMapping("/eliminar{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable("id") Long id){
        boolean ok = this.userService.deleteById(id);
        if(ok){
            return new ResponseEntity<>("El usuario con la id " + id + " ha sido eliminado", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("El usuario con la id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Cambiar el rol de un usuario", description = "Actualiza el rol de un usuario específico por su ID, retornando el usuario actualizado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Rol del usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. el nuevo rol es nulo o inválido)",
                    content = @Content(mediaType = "application/json"))
    @PutMapping("/cambiarRol/{id}")
    public ResponseEntity<EntityModel<Usuario>> cambiarRol(
                @Parameter(description = "ID del usuario cuyo rol se va a cambiar", required = true)
                @PathVariable("id") Long id,
                @Parameter(description = "Nuevo nombre del rol para el usuario", required = true)
                @RequestParam("newRoleName") String nuevoRol) {
        Usuario actualizado = userService.changeRol(id, nuevoRol);
        if (actualizado == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<Usuario> entityModel = EntityModel.of(actualizado,
            linkTo(methodOn(UsuarioController.class).getById(actualizado.getId())).withSelfRel(), // <-- ¡CORREGIDO AQUÍ!
            linkTo(methodOn(UsuarioController.class).getUsuarios()).withRel("all_users"),
            linkTo(methodOn(UsuarioController.class).update(null, actualizado.getId())).withRel("update_user"),
            linkTo(methodOn(UsuarioController.class).eliminar(actualizado.getId())).withRel("delete_user"),
            linkTo(methodOn(UsuarioController.class).cambiarRol(actualizado.getId(), null)).withRel("change_role")); // Este link puede quedarse si quieres un enlace a la operación de cambio de rol, pero no como self-rel

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

}