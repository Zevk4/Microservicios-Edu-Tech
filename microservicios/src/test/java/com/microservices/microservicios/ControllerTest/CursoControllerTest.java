package com.microservices.microservicios.ControllerTest; // Asegúrate que este paquete coincida con la ruta real de tu proyecto

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.microservicios.controller.CursoController;
import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.service.CursoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CursoController.class) // Validar la capa web de forma aislada
public class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CursoService cursService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testVerCursos() throws Exception {
        Curso curso1 = new Curso();
        curso1.setId(1L);
        curso1.setTitulo("Matematicas Avanzadas"); 
        curso1.setCategoria("Ciencias");
        curso1.setDescripcion("Curso de Matematicas para ingenieros");
        curso1.setInstructor("Dr. López");
        curso1.setPrice(75.50);
        curso1.setPopularidad(4.5);

        Curso curso2 = new Curso();
        curso2.setId(2L);
        curso2.setTitulo("Historia Universal"); 
        curso2.setCategoria("Humanidades");
        curso2.setDescripcion("Desde la antigüedad hasta el presente");
        curso2.setInstructor("Dra. García");
        curso2.setPrice(50.00);
        curso2.setPopularidad(4.2);

        ArrayList<Curso> cursos = new ArrayList<>(Arrays.asList(curso1, curso2));

        when(cursService.verCursos()).thenReturn(cursos);

        mockMvc.perform(get("/curso/vercursos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cursoList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.cursoList[0].titulo", is("Matematicas Avanzadas"))) 
                .andExpect(jsonPath("$._embedded.cursoList[0].categoria", is("Ciencias"))) 
                .andExpect(jsonPath("$._embedded.cursoList[1].titulo", is("Historia Universal"))) 
                .andExpect(jsonPath("$._embedded.cursoList[1].price", is(50.00))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/curso/vercursos")));

        verify(cursService, times(1)).verCursos();
    }

    @Test
    void testBuscarIdExistente() throws Exception {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setTitulo("Programacion Java"); 
        curso.setCategoria("Programación");
        curso.setDescripcion("Curso completo de Java para desarrolladores");
        curso.setInstructor("Ana Ramirez");
        curso.setPrice(99.99);
        curso.setPopularidad(4.9);

        when(cursService.buscarCurso(1L)).thenReturn(Optional.of(curso));

        mockMvc.perform(get("/curso/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Programacion Java"))) 
                .andExpect(jsonPath("$.price", is(99.99))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/curso/1")));

        verify(cursService, times(1)).buscarCurso(1L);
    }

    @Test
    void testBuscarIdNoExistente() throws Exception {
        when(cursService.buscarCurso(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/curso/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(cursService, times(1)).buscarCurso(99L);
    }

    @Test
    void testCrearCurso() throws Exception {
        Curso nuevoCurso = new Curso();
        nuevoCurso.setTitulo("Bases de Datos SQL"); 
        nuevoCurso.setCategoria("Bases de Datos");
        nuevoCurso.setDescripcion("Introduccion a SQL y diseño de bases de datos");
        nuevoCurso.setInstructor("Carlos Soto");
        nuevoCurso.setPrice(60.00);
        nuevoCurso.setPopularidad(4.3);

        Curso cursoGuardado = new Curso();
        cursoGuardado.setId(3L);
        cursoGuardado.setTitulo("Bases de Datos SQL");
        cursoGuardado.setCategoria("Bases de Datos");
        cursoGuardado.setDescripcion("Introduccion a SQL y diseño de bases de datos");
        cursoGuardado.setInstructor("Carlos Soto");
        cursoGuardado.setPrice(60.00);
        cursoGuardado.setPopularidad(4.3);

        when(cursService.guardar(any(Curso.class))).thenReturn(cursoGuardado);

        mockMvc.perform(post("/curso/ingresarCurso")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoCurso)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.titulo", is("Bases de Datos SQL"))) 
                .andExpect(jsonPath("$.price", is(60.00))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/curso/3")));

        verify(cursService, times(1)).guardar(any(Curso.class));
    }

    @Test
    void testActualizarCursoExistente() throws Exception {
        Curso cursoActualizado = new Curso();
        cursoActualizado.setId(1L);
        cursoActualizado.setTitulo("Matematicas Aplicadas"); 
        cursoActualizado.setCategoria("Ciencias");
        cursoActualizado.setDescripcion("Curso de Matematicas enfocado en aplicaciones");
        cursoActualizado.setInstructor("Dr. López");
        cursoActualizado.setPrice(80.00);
        cursoActualizado.setPopularidad(4.7);

        when(cursService.actualizarCurso(any(Curso.class), eq(1L))).thenReturn(cursoActualizado);

        mockMvc.perform(put("/curso/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.titulo", is("Matematicas Aplicadas"))) 
                .andExpect(jsonPath("$.price", is(80.00))) 
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/curso/1")));

        verify(cursService, times(1)).actualizarCurso(any(Curso.class), eq(1L));
    }

    @Test
    void testActualizarCursoNoExistente() throws Exception {
        Curso cursoParaActualizar = new Curso();
        cursoParaActualizar.setTitulo("Inexistente");
        cursoParaActualizar.setDescripcion("Este curso no existe");
        cursoParaActualizar.setPrice(0.00);

        when(cursService.actualizarCurso(any(Curso.class), eq(99L))).thenReturn(null);

        mockMvc.perform(put("/curso/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cursoParaActualizar)))
                .andExpect(status().isNotFound());

        verify(cursService, times(1)).actualizarCurso(any(Curso.class), eq(99L));
    }

    @Test
    void testEliminarCursoExistente() throws Exception {
        when(cursService.eliminarPorId(1L)).thenReturn(true);

        mockMvc.perform(delete("/curso/eliminar{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("El Curso con la id 1 ha sido eliminado"));

        verify(cursService, times(1)).eliminarPorId(1L);
    }

    @Test
    void testEliminarCursoNoExistente() throws Exception {
        when(cursService.eliminarPorId(99L)).thenReturn(false);

        mockMvc.perform(delete("/curso/eliminar{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El Curso con la id 99 no existe"));

        verify(cursService, times(1)).eliminarPorId(99L);
    }
}