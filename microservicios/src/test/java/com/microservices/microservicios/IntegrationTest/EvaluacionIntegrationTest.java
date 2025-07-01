package com.microservices.microservicios.IntegrationTest;

import com.microservices.microservicios.MicroserviciosApplication;
import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.repository.CursoRepository;
import com.microservices.microservicios.repository.EvaluacionRepository;
import com.microservices.microservicios.service.EvaluacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MicroserviciosApplication.class)
@ActiveProfiles("test")
@Transactional
public class EvaluacionIntegrationTest {

    @Autowired
    private EvaluacionRepository evaluacionRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private EvaluacionService evaluacionService;

    private Curso cursoExistente;

    @BeforeEach
    void setUp() {
        evaluacionRepository.deleteAll(); // Limpiar evaluaciones
        cursoRepository.deleteAll();      // Limpiar cursos
        cursoExistente = new Curso("Curso Base", "Programación", "Descripción", "Instructor Test", 100.0, 4.5);
        cursoRepository.save(cursoExistente); // Persistir un curso válido para que otros tests puedan usarlo
    }

    // --- Tests para la capa de Repositorio (validando JPA/Hibernate y mapeo) ---

    @Test
    void testRepository_GuardarNuevaEvaluacion() {
        Evaluacion nuevaEvaluacion = new Evaluacion(
            "Examen Final", "Examen de todos los temas", "Escrito",
            LocalDateTime.now(), LocalDateTime.now().plusHours(2), 120, 100.0, "Pendiente",
            cursoExistente
        );
        Evaluacion evaluacionGuardada = evaluacionRepository.save(nuevaEvaluacion);
        assertNotNull(evaluacionGuardada.getId(), "El ID de la evaluación no debería ser nulo después de guardar");
        assertEquals("Examen Final", evaluacionGuardada.getNombre());
        assertEquals(cursoExistente.getId(), evaluacionGuardada.getCurso().getId(), "El curso debe ser el asignado");
        Optional<Evaluacion> recuperada = evaluacionRepository.findById(evaluacionGuardada.getId());
        assertTrue(recuperada.isPresent());
        assertEquals("Examen Final", recuperada.get().getNombre());
        assertEquals(cursoExistente.getTitulo(), recuperada.get().getCurso().getTitulo());
    }


    @Test
    void testRepository_BuscarEvaluacionPorIdExistente() {
        Evaluacion evaluacionExistente = new Evaluacion(
            "Parcial 1", "Primera evaluación", "Escrito",
            LocalDateTime.now(), LocalDateTime.now().plusDays(1), 90, 50.0, "Realizado",
            cursoExistente
        );
        evaluacionExistente = evaluacionRepository.save(evaluacionExistente);
        Optional<Evaluacion> encontrada = evaluacionRepository.findById(evaluacionExistente.getId());
        assertTrue(encontrada.isPresent(), "La evaluación debería ser encontrada por su ID");
        assertEquals("Parcial 1", encontrada.get().getNombre());
    }

    @Test
    void testRepository_BuscarEvaluacionPorIdNoExistente() {
        Optional<Evaluacion> encontrada = evaluacionRepository.findById(999L);
        assertFalse(encontrada.isPresent(), "No se debería encontrar una evaluación con un ID inexistente");
    }

    @Test
    void testRepository_ListarTodasLasEvaluaciones() {
        evaluacionRepository.save(new Evaluacion("Tarea 1", "Individual", "Proyecto", LocalDateTime.now(), LocalDateTime.now().plusDays(5), 0, 30.0, "Asignado", cursoExistente));
        evaluacionRepository.save(new Evaluacion("Presentacion", "Grupo", "Oral", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 45, 20.0, "Pendiente", cursoExistente));
        List<Evaluacion> evaluaciones = evaluacionRepository.findAll();
        assertNotNull(evaluaciones, "La lista de evaluaciones no debería ser nula");
        assertEquals(2, evaluaciones.size());
        assertTrue(evaluaciones.stream().anyMatch(e -> e.getNombre().equals("Tarea 1")));
        assertTrue(evaluaciones.stream().anyMatch(e -> e.getNombre().equals("Presentacion")));
    }

    @Test
    void testRepository_ActualizarEvaluacionExistente() {
        Evaluacion evaluacionOriginal = new Evaluacion(
            "Examen Previo", "Examen corto", "Escrito",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 40.0, "Borrador",
            cursoExistente
        );
        evaluacionOriginal = evaluacionRepository.save(evaluacionOriginal);
        evaluacionOriginal.setNombre("Examen Final Modificado");
        evaluacionOriginal.setCalificacionMaxima(120.0);
        evaluacionOriginal.setEstado("Publicado");
        Evaluacion evaluacionActualizada = evaluacionRepository.save(evaluacionOriginal);
        assertNotNull(evaluacionActualizada);
        assertEquals(evaluacionOriginal.getId(), evaluacionActualizada.getId());
        assertEquals("Examen Final Modificado", evaluacionActualizada.getNombre());
        assertEquals(120.0, evaluacionActualizada.getCalificacionMaxima());
        assertEquals("Publicado", evaluacionActualizada.getEstado());
        Optional<Evaluacion> verificado = evaluacionRepository.findById(evaluacionActualizada.getId());
        assertTrue(verificado.isPresent());
        assertEquals("Examen Final Modificado", verificado.get().getNombre());
    }

    @Test
    void testRepository_EliminarEvaluacionExistente() {
        Evaluacion evaluacionAEliminar = new Evaluacion(
            "Prueba a eliminar", "Será borrada", "Oral",
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), 10, 10.0, "Pendiente",
            cursoExistente
        );
        evaluacionAEliminar = evaluacionRepository.save(evaluacionAEliminar);
        evaluacionRepository.deleteById(evaluacionAEliminar.getId());
        Optional<Evaluacion> eliminada = evaluacionRepository.findById(evaluacionAEliminar.getId());
        assertFalse(eliminada.isPresent(), "La evaluación debería haber sido eliminada");
    }

    // --- Tests para la capa de Servicio (validando la interacción con el repositorio y la DB) ---

    @Test
    void testService_CrearEvaluacionExitoso() {
        Evaluacion nuevaEvaluacion = new Evaluacion(
            "Examen Servicio", "Prueba de servicio", "Escrito",
            LocalDateTime.now(), LocalDateTime.now().plusHours(3), 180, 70.0, "Activa",
            cursoExistente
        );
        Evaluacion evaluacionCreada = evaluacionService.crearEvaluacion(nuevaEvaluacion);
        assertNotNull(evaluacionCreada.getId());
        assertEquals("Examen Servicio", evaluacionCreada.getNombre());
        assertEquals(cursoExistente.getId(), evaluacionCreada.getCurso().getId());
        Optional<Evaluacion> encontradaEnDB = evaluacionRepository.findById(evaluacionCreada.getId());
        assertTrue(encontradaEnDB.isPresent());
        assertEquals("Examen Servicio", encontradaEnDB.get().getNombre());
        assertEquals(cursoExistente.getTitulo(), encontradaEnDB.get().getCurso().getTitulo());
    }

    @Test
    void testService_VerTodasLasEvaluaciones() {
        evaluacionRepository.save(new Evaluacion("Eva1", "Desc1", "Tipo1", LocalDateTime.now(), LocalDateTime.now().plusDays(1), 60, 50.0, "Estado1", cursoExistente));
        evaluacionRepository.save(new Evaluacion("Eva2", "Desc2", "Tipo2", LocalDateTime.now(), LocalDateTime.now().plusDays(2), 90, 70.0, "Estado2", cursoExistente));
        List<Evaluacion> evaluaciones = evaluacionService.verEvaluacion();
        assertNotNull(evaluaciones);
        assertEquals(2, evaluaciones.size());
        assertTrue(evaluaciones.stream().anyMatch(e -> e.getNombre().equals("Eva1")));
        assertTrue(evaluaciones.stream().anyMatch(e -> e.getNombre().equals("Eva2")));
    }

    @Test
    void testService_BuscarEvaluacionPorIdExistente() {
        Evaluacion evaluacionExistente = new Evaluacion(
            "Buscar Eva", "Descripción", "Tipo",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 100.0, "Activo",
            cursoExistente
        );
        evaluacionExistente = evaluacionRepository.save(evaluacionExistente);
        Optional<Evaluacion> encontrada = evaluacionService.buscarEvaluacion(evaluacionExistente.getId());
        assertTrue(encontrada.isPresent());
        assertEquals("Buscar Eva", encontrada.get().getNombre());
    }

    @Test
    void testService_BuscarEvaluacionPorIdNoExistente() {
        Optional<Evaluacion> encontrada = evaluacionService.buscarEvaluacion(999L);
        assertFalse(encontrada.isPresent());
    }

    @Test
    void testService_ActualizarEvaluacionExistente() {
        Evaluacion evaluacionOriginal = new Evaluacion(
            "Original Eva", "Desc Orig", "Tipo Orig",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 50.0, "Estado Orig",
            cursoExistente
        );
        evaluacionOriginal = evaluacionRepository.save(evaluacionOriginal);

        Curso nuevoCurso = new Curso(
            "Nuevo Curso para Eva", "Otra Categoria", "Desc", "Ms. Jones", 120.0, 4.0
        );
        nuevoCurso = cursoRepository.save(nuevoCurso);

        Evaluacion datosActualizados = new Evaluacion(
            "Updated Eva", "Desc Updated", "Tipo Updated",
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 90, 75.0, "Estado Updated",
            nuevoCurso
        );
        Evaluacion evaluacionActualizada = evaluacionService.actualizarEvaluacion(datosActualizados, evaluacionOriginal.getId());
        assertNotNull(evaluacionActualizada);
        assertEquals(evaluacionOriginal.getId(), evaluacionActualizada.getId());
        assertEquals("Updated Eva", evaluacionActualizada.getNombre());
        assertEquals("Desc Updated", evaluacionActualizada.getDescripcion());
        assertEquals(75.0, evaluacionActualizada.getCalificacionMaxima());
        assertEquals(nuevoCurso.getId(), evaluacionActualizada.getCurso().getId());
        assertEquals("Nuevo Curso para Eva", evaluacionActualizada.getCurso().getTitulo());

        Optional<Evaluacion> verificadoEnDB = evaluacionRepository.findById(evaluacionOriginal.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("Updated Eva", verificadoEnDB.get().getNombre());
        assertEquals(nuevoCurso.getTitulo(), verificadoEnDB.get().getCurso().getTitulo());
    }

    @Test
    void testService_ActualizarEvaluacionNoExistenteDebeLanzarNoSuchElementException() {
        Evaluacion datosActualizados = new Evaluacion(
            "Eva Ficticia", "Desc Ficticia", "Tipo",
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), 60, 10.0, "Estado",
            cursoExistente
        );
        Long idInexistente = 999L;
        assertThrows(NoSuchElementException.class, () -> {
            evaluacionService.actualizarEvaluacion(datosActualizados, idInexistente);
        });
        assertEquals(1, cursoRepository.count());
        assertEquals(0, evaluacionRepository.count());
    }

    @Test
    void testService_EliminarPorIdExistente() {
        Evaluacion evaluacionAEliminar = new Evaluacion(
            "Eliminar Eva", "Se va a borrar", "Oral",
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), 10, 10.0, "Pendiente",
            cursoExistente
        );
        evaluacionAEliminar = evaluacionRepository.save(evaluacionAEliminar);
        Boolean resultado = evaluacionService.eliminarPorId(evaluacionAEliminar.getId());
        assertTrue(resultado, "La evaluación debería haber sido eliminada exitosamente");
        Optional<Evaluacion> eliminado = evaluacionRepository.findById(evaluacionAEliminar.getId());
        assertFalse(eliminado.isPresent(), "La evaluación no debería estar en la base de datos después de eliminarla");
    }

    @Test
    void testService_EliminarPorIdNoExistente() {
        Long idInexistente = 999L;
        Boolean resultado = evaluacionService.eliminarPorId(idInexistente);
        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar una evaluación inexistente");
        assertEquals(0, evaluacionRepository.count());
        assertEquals(1, cursoRepository.count());
    }
}