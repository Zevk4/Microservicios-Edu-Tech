package com.microservices.microservicios.ServiceTest;

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.repository.CursoRepository;
import com.microservices.microservicios.service.CursoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CursoServiceTest {

    @Mock // Mock del repositorio de Curso
    private CursoRepository cursRepo;

    @InjectMocks // Inyecta los mocks en la instancia de CursoService
    private CursoService cursoService;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarCurso() {
        Curso cursoEntrada = new Curso("Java Avanzado", "Programación", "Curso completo de Java", "Dr. Code", 199.99, 4.8);
        Curso cursoGuardado = new Curso("Java Avanzado", "Programación", "Curso completo de Java", "Dr. Code", 199.99, 4.8);
        cursoGuardado.setId(1L); // Simula que el repositorio le asigna un ID

        // Configura el mock para que cuando se llame a save, devuelva cursoGuardado
        when(cursRepo.save(any(Curso.class))).thenReturn(cursoGuardado);
        Curso resultado = cursoService.guardar(cursoEntrada);
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Java Avanzado", resultado.getTitulo());
        assertEquals("Dr. Code", resultado.getInstructor());
        // Verifica que el método save del repositorio fue llamado exactamente una vez con cualquier objeto Curso
        verify(cursRepo, times(1)).save(any(Curso.class));
    }

    @Test
    void testVerCursos() {
        Curso cur1 = new Curso("Python Básico", "Programación", "Introducción a Python", "Ana Smith", 49.99, 4.0);
        cur1.setId(1L);
        Curso cur2 = new Curso("Diseño UX/UI", "Diseño", "Principios de diseño", "Luis Garcia", 79.99, 4.5);
        cur2.setId(2L);

        ArrayList<Curso> cursos = new ArrayList<>(Arrays.asList(cur1, cur2));

        // Configura el mock para que cuando se llame a findAll, devuelva la lista de cursos
        when(cursRepo.findAll()).thenReturn(cursos);

        List<Curso> resultado = cursoService.verCursos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Python Básico", resultado.get(0).getTitulo());
        assertEquals("Diseño UX/UI", resultado.get(1).getTitulo());
        // Verifica que el método findAll del repositorio fue llamado exactamente una vez
        verify(cursRepo, times(1)).findAll();
    }

    @Test
    void testBuscarCursoExistente() {
        Curso curso = new Curso("Marketing Digital", "Negocios", "Estrategias de marketing", "Eva Blanco", 120.00, 4.7);
        curso.setId(3L);

        // Configura el mock para que cuando se llame a findById con 3L, devuelva el curso
        when(cursRepo.findById(3L)).thenReturn(Optional.of(curso));
        Optional<Curso> resultado = cursoService.buscarCurso(3L);

        assertTrue(resultado.isPresent());
        assertEquals(3L, resultado.get().getId());
        assertEquals("Marketing Digital", resultado.get().getTitulo());
        // Verifica que el método findById del repositorio fue llamado exactamente una vez con 3L
        verify(cursRepo, times(1)).findById(3L);
    }

    @Test
    void testBuscarCursoNoExistente() {
        // Configura el mock para que cuando se llame a findById con cualquier Long, devuelva un Optional vacío
        when(cursRepo.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.buscarCurso(99L);

        assertFalse(resultado.isPresent());
        // Verifica que el método findById del repositorio fue llamado exactamente una vez con 99L
        verify(cursRepo, times(1)).findById(99L);
    }

    @Test
    void testActualizarCursoExistente() {
        Curso cursoExistente = new Curso("Programación C++", "Programación", "C++ básico", "Pedro Coder", 80.00, 4.2);
        cursoExistente.setId(1L);
        Curso cursoActualizadoData = new Curso("Programación C++ Avanzado", "Programación", "C++ avanzado y moderno", "Pedro Coder", 150.00, 4.6); // Datos para actualizar

        // Configura mocks:
        // 1. Cuando se busque por ID, devolver el curso existente.
        when(cursRepo.findById(1L)).thenReturn(Optional.of(cursoExistente));
        // 2. Cuando se guarde la instancia modificada, devolver esa misma instancia.
        when(cursRepo.save(any(Curso.class))).thenReturn(cursoExistente); // save devolverá la misma instancia modificada

        Curso resultado = cursoService.actualizarCurso(cursoActualizadoData, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Programación C++ Avanzado", resultado.getTitulo()); // Verifica que el título fue actualizado
        assertEquals(150.00, resultado.getPrice()); // Verifica que el precio fue actualizado
        assertEquals(4.6, resultado.getPopularidad()); // Verifica la popularidad
        // Verifica que findById y save fueron llamados una vez
        verify(cursRepo, times(1)).findById(1L);
        verify(cursRepo, times(1)).save(cursoExistente); // Verifica que se guarda la instancia existente modificada
    }

    @Test
    void testActualizarCursoNoExistenteDebeLanzarExcepcion() {
        Curso cursoParaActualizar = new Curso("Curso Inexistente", "Categoria", "Desc", "Instructor", 1.0, 1.0);

        // Configura el mock para que findById devuelva un Optional vacío
        when(cursRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> {
            cursoService.actualizarCurso(cursoParaActualizar, 99L);
        });

        // Verifica que findById fue llamado una vez
        verify(cursRepo, times(1)).findById(99L);
        // Asegura que save nunca fue llamado si el findById falla
        verify(cursRepo, never()).save(any(Curso.class));
    }

    @Test
    void testEliminarPorIdExistente() {
        Long idExistente = 1L;
        // Mock: Cuando el servicio pregunte si existe, devuelve true
        when(cursRepo.existsById(idExistente)).thenReturn(true);
        // Mock: Cuando el servicio pida eliminar, no haga nada
        doNothing().when(cursRepo).deleteById(idExistente);

        Boolean resultado = cursoService.eliminarPorId(idExistente);

        assertTrue(resultado, "Debería retornar true si el curso existe y es eliminado");
        // Verifica que existsById fue llamado una vez
        verify(cursRepo, times(1)).existsById(idExistente);
        // Verifica que deleteById fue llamado una vez
        verify(cursRepo, times(1)).deleteById(idExistente);
    }

    @Test
    void testEliminarPorIdNoExistente() {
        Long idInexistente = 99L;
        // Mock: Cuando el servicio pregunte si existe, devuelve false
        when(cursRepo.existsById(idInexistente)).thenReturn(false);

        Boolean resultado = cursoService.eliminarPorId(idInexistente);

        assertFalse(resultado, "Debería retornar false si el curso no existe");
        // Verifica que existsById fue llamado una vez
        verify(cursRepo, times(1)).existsById(idInexistente);
        // Verifica que deleteById NUNCA fue llamado (porque el curso no existía)
        verify(cursRepo, never()).deleteById(anyLong()); 
    }
}