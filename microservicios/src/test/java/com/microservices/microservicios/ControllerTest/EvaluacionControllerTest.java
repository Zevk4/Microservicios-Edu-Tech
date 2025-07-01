package com.microservices.microservicios.ControllerTest; // Asegúrate que este paquete coincida con la ruta real

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microservices.microservicios.controller.EvaluacionController;
import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.service.EvaluacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluacionController.class)
public class EvaluacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EvaluacionService evaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetEvaluaciones() throws Exception {
        // Creamos un curso mock con atributos reales
        Curso cursoMock = new Curso();
        cursoMock.setId(10L);
        cursoMock.setTitulo("Curso de Spring Boot");
        cursoMock.setCategoria("Desarrollo Web");
        cursoMock.setDescripcion("Curso para aprender Spring Boot desde cero.");
        cursoMock.setInstructor("Juan Perez");
        cursoMock.setPrice(49.99);
        cursoMock.setPopularidad(4.8);

        Evaluacion eva1 = new Evaluacion();
        eva1.setId(1L);
        eva1.setNombre("Examen Final");
        eva1.setDescripcion("Examen completo del curso");
        eva1.setTipo("Examen");
        eva1.setFecha_inicio(LocalDateTime.of(2023, 1, 15, 9, 0));
        eva1.setFecha_termino(LocalDateTime.of(2023, 1, 15, 11, 0));
        eva1.setDuracion(120);
        eva1.setCalificacionMaxima(100.0);
        eva1.setEstado("Activo");
        eva1.setCurso(cursoMock);

        Evaluacion eva2 = new Evaluacion();
        eva2.setId(2L);
        eva2.setNombre("Cuestionario 1");
        eva2.setDescripcion("Preguntas de la Unidad 1");
        eva2.setTipo("Cuestionario");
        eva2.setFecha_inicio(LocalDateTime.of(2023, 2, 20, 10, 0));
        eva2.setFecha_termino(LocalDateTime.of(2023, 2, 20, 10, 30));
        eva2.setDuracion(30);
        eva2.setCalificacionMaxima(50.0);
        eva2.setEstado("Finalizado");
        eva2.setCurso(cursoMock);

        ArrayList<Evaluacion> evaluaciones = new ArrayList<>(Arrays.asList(eva1, eva2));

        when(evaService.verEvaluacion()).thenReturn(evaluaciones);

        mockMvc.perform(get("/evaluacion/evaluaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.evaluacionList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.evaluacionList[0].nombre", is("Examen Final")))
                .andExpect(jsonPath("$._embedded.evaluacionList[0].calificacionMaxima", is(100.0)))
                .andExpect(jsonPath("$._embedded.evaluacionList[0].curso.titulo", is("Curso de Spring Boot"))) 
                .andExpect(jsonPath("$._embedded.evaluacionList[1].tipo", is("Cuestionario")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/evaluacion/evaluaciones")));

        verify(evaService, times(1)).verEvaluacion();
    }

    @Test
    void testCrearEvaluacion() throws Exception {
        Curso cursoMock = new Curso();
        cursoMock.setId(10L);
        cursoMock.setTitulo("Curso de Spring Boot");
        cursoMock.setCategoria("Desarrollo Web");
        cursoMock.setDescripcion("Curso para aprender Spring Boot desde cero.");
        cursoMock.setInstructor("Juan Perez");
        cursoMock.setPrice(49.99);
        cursoMock.setPopularidad(4.8);

        Evaluacion nuevaEva = new Evaluacion();
        nuevaEva.setNombre("Tarea #1");
        nuevaEva.setDescripcion("Resolver ejercicios");
        nuevaEva.setTipo("Tarea");
        nuevaEva.setFecha_inicio(LocalDateTime.of(2024, 5, 10, 8, 0));
        nuevaEva.setFecha_termino(LocalDateTime.of(2024, 5, 17, 23, 59));
        nuevaEva.setDuracion(null);
        nuevaEva.setCalificacionMaxima(25.0);
        nuevaEva.setEstado("Pendiente");
        nuevaEva.setCurso(cursoMock);

        Evaluacion evaGuardada = new Evaluacion();
        evaGuardada.setId(3L);
        evaGuardada.setNombre("Tarea #1");
        evaGuardada.setDescripcion("Resolver ejercicios");
        evaGuardada.setTipo("Tarea");
        evaGuardada.setFecha_inicio(LocalDateTime.of(2024, 5, 10, 8, 0));
        evaGuardada.setFecha_termino(LocalDateTime.of(2024, 5, 17, 23, 59));
        evaGuardada.setDuracion(null);
        evaGuardada.setCalificacionMaxima(25.0);
        evaGuardada.setEstado("Pendiente");
        evaGuardada.setCurso(cursoMock);


        when(evaService.crearEvaluacion(any(Evaluacion.class))).thenReturn(evaGuardada);

        mockMvc.perform(post("/evaluacion/crearEvaluacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaEva)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Tarea #1")))
                .andExpect(jsonPath("$.calificacionMaxima", is(25.0)))
                .andExpect(jsonPath("$.curso.titulo", is("Curso de Spring Boot"))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/evaluacion/3")));

        verify(evaService, times(1)).crearEvaluacion(any(Evaluacion.class));
    }

    @Test
    void testBuscarEvaluacionExistente() throws Exception {
        Curso cursoMock = new Curso();
        cursoMock.setId(10L);
        cursoMock.setTitulo("Curso de Spring Boot");
        cursoMock.setCategoria("Desarrollo Web");
        cursoMock.setDescripcion("Curso para aprender Spring Boot desde cero.");
        cursoMock.setInstructor("Juan Perez");
        cursoMock.setPrice(49.99);
        cursoMock.setPopularidad(4.8);

        Evaluacion eva = new Evaluacion();
        eva.setId(1L);
        eva.setNombre("Examen Parcial");
        eva.setDescripcion("Examen de medio curso");
        eva.setTipo("Examen");
        eva.setFecha_inicio(LocalDateTime.of(2023, 3, 5, 14, 0));
        eva.setFecha_termino(LocalDateTime.of(2023, 3, 5, 15, 30));
        eva.setDuracion(90);
        eva.setCalificacionMaxima(70.0);
        eva.setEstado("Completado");
        eva.setCurso(cursoMock);

        when(evaService.buscarEvaluacion(1L)).thenReturn(Optional.of(eva));

        mockMvc.perform(get("/evaluacion/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Examen Parcial")))
                .andExpect(jsonPath("$.calificacionMaxima", is(70.0)))
                .andExpect(jsonPath("$.curso.titulo", is("Curso de Spring Boot"))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/evaluacion/1")));

        verify(evaService, times(1)).buscarEvaluacion(1L);
    }

    @Test
    void testBuscarEvaluacionNoExistente() throws Exception {
        when(evaService.buscarEvaluacion(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/evaluacion/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(evaService, times(1)).buscarEvaluacion(99L);
    }

    @Test
    void testActualizarEvaluacionExistente() throws Exception {
        Curso cursoMock = new Curso();
        cursoMock.setId(10L);
        cursoMock.setTitulo("Curso de Spring Boot");
        cursoMock.setCategoria("Desarrollo Web");
        cursoMock.setDescripcion("Curso para aprender Spring Boot desde cero.");
        cursoMock.setInstructor("Juan Perez");
        cursoMock.setPrice(49.99);
        cursoMock.setPopularidad(4.8);

        Evaluacion evaActualizada = new Evaluacion();
        evaActualizada.setId(1L);
        evaActualizada.setNombre("Examen Final (Revisado)");
        evaActualizada.setDescripcion("Examen final del curso con correcciones");
        evaActualizada.setTipo("Examen");
        evaActualizada.setFecha_inicio(LocalDateTime.of(2023, 1, 15, 9, 0));
        evaActualizada.setFecha_termino(LocalDateTime.of(2023, 1, 15, 11, 0));
        evaActualizada.setDuracion(120);
        evaActualizada.setCalificacionMaxima(100.0);
        evaActualizada.setEstado("Revisado");
        evaActualizada.setCurso(cursoMock);

        when(evaService.actualizarEvaluacion(any(Evaluacion.class), eq(1L))).thenReturn(evaActualizada);

        mockMvc.perform(put("/evaluacion/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evaActualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Examen Final (Revisado)")))
                .andExpect(jsonPath("$.calificacionMaxima", is(100.0)))
                .andExpect(jsonPath("$.curso.titulo", is("Curso de Spring Boot"))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/evaluacion/1")));

        verify(evaService, times(1)).actualizarEvaluacion(any(Evaluacion.class), eq(1L));
    }

    @Test
    void testActualizarEvaluacionNoExistente() throws Exception {
        Evaluacion evaParaActualizar = new Evaluacion();
        evaParaActualizar.setNombre("No existente");
        evaParaActualizar.setCalificacionMaxima(50.0);
        when(evaService.actualizarEvaluacion(any(Evaluacion.class), eq(99L))).thenReturn(null);

        mockMvc.perform(put("/evaluacion/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(evaParaActualizar)))
                .andExpect(status().isNotFound());

        verify(evaService, times(1)).actualizarEvaluacion(any(Evaluacion.class), eq(99L));
    }

    @Test
    void testEliminarEvaluacionExistente() throws Exception {
        when(evaService.eliminarPorId(1L)).thenReturn(true);

        mockMvc.perform(delete("/evaluacion/eliminar{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("La Evaluacion con la id 1 ha sido eliminada"));

        verify(evaService, times(1)).eliminarPorId(1L);
    }

    @Test
    void testEliminarEvaluacionNoExistente() throws Exception {
        when(evaService.eliminarPorId(99L)).thenReturn(false);

        mockMvc.perform(delete("/evaluacion/eliminar{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("La Evaluacion con la id 99 no existe"));

        verify(evaService, times(1)).eliminarPorId(99L);
    }
}