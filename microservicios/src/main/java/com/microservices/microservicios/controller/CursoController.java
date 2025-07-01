package com.microservices.microservicios.controller;
import java.util.List; // Importar List para la colección
import java.util.Optional;
import java.util.stream.Collectors; // Para usar .stream() y .collect()

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*; // Importar estáticamente linkTo y methodOn
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // Para devolver ResponseEntity en algunos casos

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.service.CursoService;

// Importaciones de OpenAPI (Swagger)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/curso")
@Tag(name = "Cursos", description = "API para la gestión de cursos")
public class CursoController {

    @Autowired
    private CursoService cursService;

    @Operation(summary = "Obtener todos los cursos", description = "Retorna una lista de todos los cursos disponibles, con enlaces HATEOAS para cada curso y la colección.")
    @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionModel.class)))
    @GetMapping("/vercursos")
    public CollectionModel<EntityModel<Curso>> verCursos(){
        List<EntityModel<Curso>> cursos = cursService.verCursos().stream()
            .map(curso -> EntityModel.of(curso,
                linkTo(methodOn(CursoController.class).buscarId(curso.getId())).withSelfRel(),
                linkTo(methodOn(CursoController.class).verCursos()).withRel("all_courses")))
            .collect(Collectors.toList());

        return CollectionModel.of(cursos, linkTo(methodOn(CursoController.class).verCursos()).withSelfRel());
    }

    @Operation(summary = "Crear un nuevo curso", description = "Crea un nuevo curso y lo guarda en la base de datos, retornando el curso creado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "201", description = "Curso creado exitosamente", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class))) 
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del curso incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PostMapping("/ingresarCurso")
    public ResponseEntity<EntityModel<Curso>> crearCurso(
            @RequestBody Curso curs){
        Curso newCurso = cursService.guardar(curs);
        EntityModel<Curso> entityModel = EntityModel.of(newCurso,
            linkTo(methodOn(CursoController.class).buscarId(newCurso.getId())).withSelfRel(),
            linkTo(methodOn(CursoController.class).verCursos()).withRel("all_courses"),
            linkTo(methodOn(CursoController.class).actualizar(null, newCurso.getId())).withRel("update_course"),
            linkTo(methodOn(CursoController.class).eliminar(newCurso.getId())).withRel("delete_course"));

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED); // Devolver 201 Created
    }

    @Operation(summary = "Buscar curso por ID", description = "Retorna un curso específico por su ID, con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Curso encontrado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class))) 
    @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                 content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Curso>> buscarId(
            @Parameter(description = "ID del curso a buscar", required = true)
            @PathVariable("id") Long id){
        Optional<Curso> curso = cursService.buscarCurso(id);

        return curso.map(curs -> {
            EntityModel<Curso> entityModel = EntityModel.of(curs,
                linkTo(methodOn(CursoController.class).buscarId(curs.getId())).withSelfRel(),
                linkTo(methodOn(CursoController.class).verCursos()).withRel("all_courses"),
                linkTo(methodOn(CursoController.class).actualizar(null, curs.getId())).withRel("update_course"),
                linkTo(methodOn(CursoController.class).eliminar(curs.getId())).withRel("delete_course"));
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar un curso por ID", description = "Actualiza la información de un curso existente, retornando el curso actualizado con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Curso actualizado exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class))) 
    @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                 content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos del curso incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PutMapping(path = "{id}")
    public ResponseEntity<EntityModel<Curso>> actualizar(
            @RequestBody Curso curs,
            @Parameter(description = "ID del curso a actualizar", required = true)
            @PathVariable Long id){
        Curso updatedCurso = cursService.actualizarCurso(curs, id);
        if (updatedCurso == null) { 
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<Curso> entityModel = EntityModel.of(updatedCurso,
            linkTo(methodOn(CursoController.class).buscarId(updatedCurso.getId())).withSelfRel(),
            linkTo(methodOn(CursoController.class).verCursos()).withRel("all_courses"),
            linkTo(methodOn(CursoController.class).crearCurso(null)).withRel("create_course"),
            linkTo(methodOn(CursoController.class).eliminar(updatedCurso.getId())).withRel("delete_course"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar un curso por ID", description = "Elimina un curso de la base de datos, retornando un mensaje de éxito o fracaso.")
    @ApiResponse(responseCode = "200", description = "Curso eliminado exitosamente",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "404", description = "Curso no encontrado para eliminar",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @DeleteMapping("/eliminar{id}")
    public ResponseEntity<String> eliminar( 
            @Parameter(description = "ID del curso a eliminar", required = true)
            @PathVariable("id") Long id){
        boolean ok = this.cursService.eliminarPorId(id);
        if(ok){
            return new ResponseEntity<>("El Curso con la id " + id + " ha sido eliminado", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("El Curso con la id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }
}