package com.microservices.microservicios.ServiceTest; // Verifica que este paquete sea el correcto

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.repository.EvaluacionRepository;
import com.microservices.microservicios.service.EvaluacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EvaluacionServiceTest {

    @Mock // Mockea el repositorio
    private EvaluacionRepository evaRepo;

    @InjectMocks // Inyecta los mocks en la instancia de EvaluacionService
    private EvaluacionService evaluacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa los mocks antes de cada prueba
    }

    // Método de utilidad para crear un Curso mock
    private Curso createMockCurso() {
        Curso curso = new Curso();
        curso.setId(10L);
        curso.setTitulo("Curso de Prueba");
        curso.setCategoria("Tecnologia");
        curso.setDescripcion("Descripcion de prueba");
        curso.setInstructor("Profesor Mock");
        curso.setPrice(99.99);
        curso.setPopularidad(4.5);
        return curso;
    }

    @Test
    void testCrearEvaluacion() {
        // Arrange
        Curso curso = createMockCurso();
        Evaluacion evaluacionEntrada = new Evaluacion("Examen Inicial", "Primer examen", "Examen",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 100.0, "Pendiente", curso);
        Evaluacion evaluacionGuardada = new Evaluacion("Examen Inicial", "Primer examen", "Examen",
                LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 100.0, "Pendiente", curso);
        evaluacionGuardada.setId(1L); // Asignamos un ID para simular que fue guardada

        when(evaRepo.save(any(Evaluacion.class))).thenReturn(evaluacionGuardada);

        // Act
        Evaluacion resultado = evaluacionService.crearEvaluacion(evaluacionEntrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Examen Inicial", resultado.getNombre());
        assertEquals("Examen", resultado.getTipo());
        assertNotNull(resultado.getCurso());
        assertEquals(curso.getTitulo(), resultado.getCurso().getTitulo());
        verify(evaRepo, times(1)).save(any(Evaluacion.class));
    }

    @Test
    void testVerEvaluacion() {
        // Arrange
        Curso curso = createMockCurso();
        Evaluacion eva1 = new Evaluacion("Examen Final", "Examen completo", "Examen",
                LocalDateTime.now(), LocalDateTime.now().plusHours(2), 120, 100.0, "Activo", curso);
        eva1.setId(1L);
        Evaluacion eva2 = new Evaluacion("Quiz 1", "Preguntas cortas", "Cuestionario",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), 30, 20.0, "Finalizado", curso);
        eva2.setId(2L);

        ArrayList<Evaluacion> evaluaciones = new ArrayList<>(Arrays.asList(eva1, eva2));

        when(evaRepo.findAll()).thenReturn(evaluaciones);

        // Act
        ArrayList<Evaluacion> resultado = evaluacionService.verEvaluacion();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Examen Final", resultado.get(0).getNombre());
        assertEquals("Quiz 1", resultado.get(1).getNombre());
        verify(evaRepo, times(1)).findAll();
    }

    @Test
    void testBuscarEvaluacionExistente() {
        // Arrange
        Curso curso = createMockCurso();
        Evaluacion evaluacion = new Evaluacion("Proyecto", "Proyecto final", "Proyecto",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), null, 50.0, "Pendiente", curso);
        evaluacion.setId(1L);

        when(evaRepo.findById(1L)).thenReturn(Optional.of(evaluacion));

        // Act
        Optional<Evaluacion> resultado = evaluacionService.buscarEvaluacion(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Proyecto", resultado.get().getNombre());
        verify(evaRepo, times(1)).findById(1L);
    }

    @Test
    void testBuscarEvaluacionNoExistente() {
        // Arrange
        when(evaRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Evaluacion> resultado = evaluacionService.buscarEvaluacion(99L);

        // Assert
        assertFalse(resultado.isPresent());
        verify(evaRepo, times(1)).findById(99L);
    }

    @Test
    void testActualizarEvaluacionExistente() {
        // Arrange
        Curso curso = createMockCurso();
        Evaluacion evaluacionExistente = new Evaluacion("Tarea", "Tarea semanal", "Tarea",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 10.0, "Pendiente", curso);
        evaluacionExistente.setId(1L);

        Evaluacion evaluacionActualizadaData = new Evaluacion("Tarea Corregida", "Tarea con correcciones", "Tarea",
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), 60, 15.0, "Revisado", curso);

        when(evaRepo.findById(1L)).thenReturn(Optional.of(evaluacionExistente));
        when(evaRepo.save(any(Evaluacion.class))).thenReturn(evaluacionActualizadaData); // El save devolverá los datos actualizados

        // Act
        Evaluacion resultado = evaluacionService.actualizarEvaluacion(evaluacionActualizadaData, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Tarea Corregida", resultado.getNombre());
        assertEquals("Revisado", resultado.getEstado());
        assertEquals(15.0, resultado.getCalificacionMaxima());
        assertEquals(60, resultado.getDuracion());
        verify(evaRepo, times(1)).findById(1L);
        verify(evaRepo, times(1)).save(evaluacionExistente); // Verifica que se guarde la instancia modificada
    }

    @Test
    void testActualizarEvaluacionNoExistenteDebeLanzarExcepcion() {
        // Arrange
        Evaluacion evaluacionActualizadaData = new Evaluacion("Cualquier Nombre", "Cualquier Desc", "Tipo",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 10.0, "Estado", createMockCurso());

        when(evaRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        // El método .get() en un Optional.empty() lanza NoSuchElementException
        assertThrows(java.util.NoSuchElementException.class, () -> {
            evaluacionService.actualizarEvaluacion(evaluacionActualizadaData, 99L);
        });

        verify(evaRepo, times(1)).findById(99L);
        verify(evaRepo, never()).save(any(Evaluacion.class)); // Asegura que save nunca fue llamado
    }

    @Test
    void testEliminarPorIdExistente() {
        // Arrange
        doNothing().when(evaRepo).deleteById(1L); // Configura el mock para que deleteById no haga nada (éxito)

        // Act
        Boolean resultado = evaluacionService.eliminarPorId(1L);

        // Assert
        assertTrue(resultado);
        verify(evaRepo, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarPorIdNoExistente() {
        // Arrange
        // Simula que deleteById lanza una excepción (por ejemplo, EmptyResultDataAccessException si JPA lo hace)
        // Aunque tu catch atrapa "Exception", simularemos una común para deleciones fallidas
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(evaRepo).deleteById(99L);

        // Act
        Boolean resultado = evaluacionService.eliminarPorId(99L);

        // Assert
        assertFalse(resultado);
        verify(evaRepo, times(1)).deleteById(99L);
    }
}