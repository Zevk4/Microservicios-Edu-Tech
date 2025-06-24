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

import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.repository.EvaluacionRepository;
import com.microservices.microservicios.service.EvaluacionService;

class EvaluacionServiceTest {

    @InjectMocks
    private EvaluacionService evaluacionService;

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearEvaluacion() {
        Evaluacion evaluacion = new Evaluacion("Examen", "Descripcion", "Tipo", null, null, 60, 100, "Activo", null);

        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        Evaluacion result = evaluacionService.crearEvaluacion(evaluacion);

        assertNotNull(result);
        assertEquals("Examen", result.getNombre());
    }

    @Test
    void testVerEvaluacion() {
        ArrayList<Evaluacion> evaluaciones = new ArrayList<>();
        evaluaciones.add(new Evaluacion("Examen", "Descripcion", "Tipo", null, null, 60, 100, "Activo", null));

        when(evaluacionRepository.findAll()).thenReturn(evaluaciones);

        ArrayList<Evaluacion> result = evaluacionService.verEvaluacion();

        assertEquals(1, result.size());
        assertEquals("Examen", result.get(0).getNombre());
    }

    @Test
    void testBuscarEvaluacion() {
        Evaluacion evaluacion = new Evaluacion("Examen", "Descripcion", "Tipo", null, null, 60, 100, "Activo", null);

        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        Optional<Evaluacion> result = evaluacionService.buscarEvaluacion(1L);

        assertTrue(result.isPresent());
        assertEquals("Examen", result.get().getNombre());
    }

    @Test
    void testActualizarEvaluacion() {
        Evaluacion evaluacion = new Evaluacion("Examen", "Descripcion", "Tipo", null, null, 60, 100, "Activo", null);
        Evaluacion updatedEvaluacion = new Evaluacion("Quiz", "Nueva Descripcion", "Nuevo Tipo", null, null, 30, 50, "Inactivo", null);

        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(updatedEvaluacion);

        Evaluacion result = evaluacionService.actualizarEvaluacion(updatedEvaluacion, 1L);

        assertNotNull(result);
        assertEquals("Quiz", result.getNombre());
    }

    @Test
    void testEliminarPorId() {
        doNothing().when(evaluacionRepository).deleteById(1L);

        Boolean result = evaluacionService.eliminarPorId(1L);

        assertTrue(result);
    }
}