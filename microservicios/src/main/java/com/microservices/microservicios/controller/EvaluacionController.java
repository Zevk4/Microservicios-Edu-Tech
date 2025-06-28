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

import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.service.EvaluacionService;

// Importaciones de OpenAPI (Swagger)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/evaluacion")
@Tag(name = "Evaluaciones", description = "API para la gestión de evaluaciones de cursos")
public class EvaluacionController {

    @Autowired
    private EvaluacionService evaService;

    @Operation(summary = "Obtener todas las evaluaciones", description = "Retorna una lista de todas las evaluaciones disponibles, con enlaces HATEOAS para cada evaluación y la colección.")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CollectionModel.class)))
    @GetMapping("/evaluaciones")
    public CollectionModel<EntityModel<Evaluacion>> getEvaluaciones(){
        List<EntityModel<Evaluacion>> evaluaciones = evaService.verEvaluacion().stream()
            .map(eva -> EntityModel.of(eva,
                linkTo(methodOn(EvaluacionController.class).buscarEvaluacion(eva.getId())).withSelfRel(),
                linkTo(methodOn(EvaluacionController.class).getEvaluaciones()).withRel("all_evaluations")))
            .collect(Collectors.toList());

        return CollectionModel.of(evaluaciones, linkTo(methodOn(EvaluacionController.class).getEvaluaciones()).withSelfRel());
    }

    @Operation(summary = "Crear una nueva evaluación", description = "Crea una nueva evaluación y la guarda en la base de datos, retornando la evaluación creada con enlaces HATEOAS.")
    @ApiResponse(responseCode = "201", description = "Evaluación creada exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos de la evaluación incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PostMapping("/crearEvaluacion")
    public ResponseEntity<EntityModel<Evaluacion>> crearEvaluacion(
            @RequestBody Evaluacion eva){
        Evaluacion newEva = evaService.crearEvaluacion(eva);
        EntityModel<Evaluacion> entityModel = EntityModel.of(newEva,
            linkTo(methodOn(EvaluacionController.class).buscarEvaluacion(newEva.getId())).withSelfRel(),
            linkTo(methodOn(EvaluacionController.class).getEvaluaciones()).withRel("all_evaluations"),
            linkTo(methodOn(EvaluacionController.class).atualizarEvaluacion(null, newEva.getId())).withRel("update_evaluation"),
            linkTo(methodOn(EvaluacionController.class).eliminarEvaluacion(newEva.getId())).withRel("delete_evaluation"));

        return new ResponseEntity<>(entityModel, HttpStatus.CREATED);
    }

    @Operation(summary = "Buscar evaluación por ID", description = "Retorna una evaluación específica por su ID, con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Evaluación encontrada exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Evaluación no encontrada",
                 content = @Content(mediaType = "application/json"))
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Evaluacion>> buscarEvaluacion(
            @Parameter(description = "ID de la evaluación a buscar", required = true)
            @PathVariable("id") Long id){
        Optional<Evaluacion> evaluacion = evaService.buscarEvaluacion(id);

        return evaluacion.map(eva -> {
            EntityModel<Evaluacion> entityModel = EntityModel.of(eva,
                linkTo(methodOn(EvaluacionController.class).buscarEvaluacion(eva.getId())).withSelfRel(),
                linkTo(methodOn(EvaluacionController.class).getEvaluaciones()).withRel("all_evaluations"),
                linkTo(methodOn(EvaluacionController.class).atualizarEvaluacion(null, eva.getId())).withRel("update_evaluation"),
                linkTo(methodOn(EvaluacionController.class).eliminarEvaluacion(eva.getId())).withRel("delete_evaluation"));
            return new ResponseEntity<>(entityModel, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Actualizar una evaluación por ID", description = "Actualiza la información de una evaluación existente, retornando la evaluación actualizada con enlaces HATEOAS.")
    @ApiResponse(responseCode = "200", description = "Evaluación actualizada exitosamente",
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntityModel.class)))
    @ApiResponse(responseCode = "404", description = "Evaluación no encontrada",
                 content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Solicitud inválida (ej. datos de la evaluación incompletos)",
                 content = @Content(mediaType = "application/json"))
    @PutMapping(path = "{id}")
    public ResponseEntity<EntityModel<Evaluacion>> atualizarEvaluacion(
            @RequestBody Evaluacion eva,
            @Parameter(description = "ID de la evaluación a actualizar", required = true)
            @PathVariable Long id){
        Evaluacion updatedEva = evaService.actualizarEvaluacion(eva, id);
        if (updatedEva == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EntityModel<Evaluacion> entityModel = EntityModel.of(updatedEva,
            linkTo(methodOn(EvaluacionController.class).buscarEvaluacion(updatedEva.getId())).withSelfRel(),
            linkTo(methodOn(EvaluacionController.class).getEvaluaciones()).withRel("all_evaluations"),
            linkTo(methodOn(EvaluacionController.class).crearEvaluacion(null)).withRel("create_evaluation"),
            linkTo(methodOn(EvaluacionController.class).eliminarEvaluacion(updatedEva.getId())).withRel("delete_evaluation"));

        return new ResponseEntity<>(entityModel, HttpStatus.OK);
    }

    @Operation(summary = "Eliminar una evaluación por ID", description = "Elimina una evaluación de la base de datos, retornando un mensaje de éxito o fracaso.")
    @ApiResponse(responseCode = "200", description = "Evaluación eliminada exitosamente",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @ApiResponse(responseCode = "404", description = "Evaluación no encontrada para eliminar",
                 content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    @DeleteMapping("/eliminar{id}")
    public ResponseEntity<String> eliminarEvaluacion(
            @Parameter(description = "ID de la evaluación a eliminar", required = true)
            @PathVariable("id") Long id){
        boolean ok = this.evaService.eliminarPorId(id);
        if(ok){
            return new ResponseEntity<>("La Evaluacion con la id " + id + " ha sido eliminada", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("La Evaluacion con la id " + id + " no existe", HttpStatus.NOT_FOUND);
        }
    }

}