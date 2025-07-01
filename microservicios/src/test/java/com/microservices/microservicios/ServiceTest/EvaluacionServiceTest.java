package com.microservices.microservicios.ServiceTest;

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

    @Mock
    private EvaluacionRepository evaRepo;

    @InjectMocks
    private EvaluacionService evaluacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
        evaluacionGuardada.setId(1L);

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
        when(evaRepo.save(any(Evaluacion.class))).thenReturn(evaluacionActualizadaData);

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
        verify(evaRepo, times(1)).save(evaluacionExistente);
    }

    @Test
    void testActualizarEvaluacionNoExistenteDebeLanzarExcepcion() {
        // Arrange
        Evaluacion evaluacionActualizadaData = new Evaluacion("Cualquier Nombre", "Cualquier Desc", "Tipo",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, 10.0, "Estado", createMockCurso());

        when(evaRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(java.util.NoSuchElementException.class, () -> {
            evaluacionService.actualizarEvaluacion(evaluacionActualizadaData, 99L);
        });

        verify(evaRepo, times(1)).findById(99L);
        verify(evaRepo, never()).save(any(Evaluacion.class));
    }

    @Test
    void testEliminarPorIdExistente() {
        // Arrange
        Long idExistente = 1L;
        // Mockea que el ID existe
        when(evaRepo.existsById(idExistente)).thenReturn(true); 
        // Mockea el comportamiento de deleteById
        doNothing().when(evaRepo).deleteById(idExistente);

        // Act
        Boolean resultado = evaluacionService.eliminarPorId(idExistente);

        // Assert
        assertTrue(resultado, "El servicio debería indicar que se pudo eliminar un curso existente");
        // Verifica que existsById fue llamado una vez
        verify(evaRepo, times(1)).existsById(idExistente); // <-- Nueva verificación
        // Verifica que deleteById fue llamado una vez
        verify(evaRepo, times(1)).deleteById(idExistente);
    }

    @Test
    void testEliminarPorIdNoExistente() {
        // Arrange
        Long idNoExistente = 99L;
        // Mockea que el ID NO existe
        when(evaRepo.existsById(idNoExistente)).thenReturn(false); 

        Boolean resultado = evaluacionService.eliminarPorId(idNoExistente);

        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar un curso inexistente");
        // Asegurarse de que existsById fue llamado una vez
        verify(evaRepo, times(1)).existsById(idNoExistente);
        // Asegurarse de que deleteById NUNCA fue llamado, porque el servicio no debería intentarlo si no existe
        verify(evaRepo, never()).deleteById(anyLong()); 
    }
}