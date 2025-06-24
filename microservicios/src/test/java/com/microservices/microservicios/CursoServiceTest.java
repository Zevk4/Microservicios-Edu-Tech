package com.microservices.microservicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.repository.CursoRepository;
import com.microservices.microservicios.service.CursoService;

class CursoServiceTest {

    @InjectMocks
    private CursoService cursoService;

    @Mock
    private CursoRepository cursoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardar() {
        Curso curso = new Curso("Java", "Programación", "Curso básico de Java", "Instructor", 100.0, 5);

        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Curso result = cursoService.guardar(curso);

        assertNotNull(result);
        assertEquals("Java", result.getTitulo());
    }

    @Test
    void testVerCursos() {
        ArrayList<Curso> cursos = new ArrayList<>();
        cursos.add(new Curso("Java", "Programación", "Curso básico de Java", "Instructor", 100.0, 5));

        when(cursoRepository.findAll()).thenReturn(cursos);

        ArrayList<Curso> result = cursoService.verCursos();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getTitulo());
    }

    @Test
    void testBuscarCurso() {
        Curso curso = new Curso("Java", "Programación", "Curso básico de Java", "Instructor", 100.0, 5);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Optional<Curso> result = cursoService.buscarCurso(1L);

        assertTrue(result.isPresent());
        assertEquals("Java", result.get().getTitulo());
    }

    @Test
    void testActualizarCurso() {
        Curso curso = new Curso("Java", "Programación", "Curso básico de Java", "Instructor", 100.0, 5);
        Curso updatedCurso = new Curso("Python", "Programación", "Curso básico de Python", "Instructor", 120.0, 4);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(updatedCurso);

        Curso result = cursoService.actualizarCurso(updatedCurso, 1L);

        assertNotNull(result);
        assertEquals("Python", result.getTitulo());
    }

    @Test
    void testEliminarPorId() {
        doNothing().when(cursoRepository).deleteById(1L);

        Boolean result = cursoService.eliminarPorId(1L);

        assertTrue(result);
    }
}