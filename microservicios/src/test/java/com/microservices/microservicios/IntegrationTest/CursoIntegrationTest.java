package com.microservices.microservicios.IntegrationTest;

import com.microservices.microservicios.MicroserviciosApplication;
import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.repository.CursoRepository; 
import com.microservices.microservicios.service.CursoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MicroserviciosApplication.class)
@ActiveProfiles("test") 
@Transactional // Asegura que cada test se ejecute en una transacción y se haga rollback al final
public class CursoIntegrationTest {

    @Autowired
    private CursoRepository cursoRepository; // Inyecta el repositorio real

    @Autowired
    private CursoService cursoService; // Inyecta el servicio real

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test para asegurar un estado limpio e independiente
        cursoRepository.deleteAll();
    }

    // Tests para la capa de Repositorio (validando JPA/Hibernate y mapeo)

    @Test
    void testRepository_GuardarNuevoCurso() {
        
        // Usamos el constructor sin ID
        Curso nuevoCurso = new Curso("Fundamentos de Algoritmos", "Informática", "Introducción a la lógica de programación", "Ada Lovelace", 75.0, 4.2);

        Curso cursoGuardado = cursoRepository.save(nuevoCurso);

        assertNotNull(cursoGuardado.getId(), "El ID del curso no debería ser nulo después de guardar");
        assertEquals("Fundamentos de Algoritmos", cursoGuardado.getTitulo());
        assertEquals("Informática", cursoGuardado.getCategoria());
        assertEquals(75.0, cursoGuardado.getPrice());
        Optional<Curso> recuperado = cursoRepository.findById(cursoGuardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals("Fundamentos de Algoritmos", recuperado.get().getTitulo());
    }

    @Test
    void testRepository_BuscarCursoPorIdExistente() {
        Curso cursoExistente = new Curso("Bases de Datos Relacionales", "Datos", "Diseño y uso de SQL", "Dr. Codd", 90.0, 4.7);
        cursoExistente = cursoRepository.save(cursoExistente); // Guardar para obtener el ID
        Optional<Curso> encontrado = cursoRepository.findById(cursoExistente.getId());

        assertTrue(encontrado.isPresent(), "El curso debería ser encontrado por su ID");
        assertEquals("Bases de Datos Relacionales", encontrado.get().getTitulo());
    }

    @Test
    void testRepository_BuscarCursoPorIdNoExistente() {
        Optional<Curso> encontrado = cursoRepository.findById(999L); // Un ID que no existe
        assertFalse(encontrado.isPresent(), "No se debería encontrar un curso con un ID inexistente");
    }

    @Test
    void testRepository_ListarTodosLosCursos() {
        cursoRepository.save(new Curso("Seguridad Informática", "Ciberseguridad", "Amenazas y defensas", "Ms. Firewall", 110.0, 4.9));
        cursoRepository.save(new Curso("Cloud Computing", "Infraestructura", "Servicios en la nube", "Mr. Cloud", 130.0, 4.8));

        List<Curso> cursos = cursoRepository.findAll();

        assertNotNull(cursos, "La lista de cursos no debería ser nula");
        assertEquals(2, cursos.size(), "Debería haber 2 cursos en la lista");
        assertTrue(cursos.stream().anyMatch(c -> c.getTitulo().equals("Seguridad Informática")));
        assertTrue(cursos.stream().anyMatch(c -> c.getTitulo().equals("Cloud Computing")));
    }

    @Test
    void testRepository_ActualizarCursoExistente() {
        Curso cursoOriginal = new Curso("Desarrollo Móvil con Android", "Móvil", "Creación de apps Android", "DevDroid", 95.0, 4.5);
        cursoOriginal = cursoRepository.save(cursoOriginal); // Guardar para obtener el ID

        cursoOriginal.setTitulo("Desarrollo Móvil con Android Avanzado");
        cursoOriginal.setPrice(105.0);
        cursoOriginal.setPopularidad(4.6);
        Curso cursoActualizado = cursoRepository.save(cursoOriginal); // Vuelve a guardar el objeto modificado

        assertNotNull(cursoActualizado);
        assertEquals("Desarrollo Móvil con Android Avanzado", cursoActualizado.getTitulo());
        assertEquals(105.0, cursoActualizado.getPrice());
        
        // Verificar que el cambio se ha persistido
        Optional<Curso> verificado = cursoRepository.findById(cursoActualizado.getId());
        assertTrue(verificado.isPresent());
        assertEquals("Desarrollo Móvil con Android Avanzado", verificado.get().getTitulo());
    }

    @Test
    void testRepository_EliminarCursoExistente() {
        Curso cursoAEliminar = new Curso("Introducción a la Inteligencia Artificial", "IA", "Conceptos básicos de IA", "Prof. AI", 150.0, 4.9);
        cursoAEliminar = cursoRepository.save(cursoAEliminar); // Guardar para obtener el ID

        cursoRepository.deleteById(cursoAEliminar.getId());

        Optional<Curso> eliminado = cursoRepository.findById(cursoAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El curso debería haber sido eliminado");
    }

    // --- Tests para la capa de Servicio (validando la interacción con el repositorio y la DB) ---

    @Test
    void testService_GuardarNuevoCurso() {
        Curso nuevoCurso = new Curso("Gestión de Proyectos Agile", "Gestión", "Scrum y Kanban", "Project Guru", 85.0, 4.7);

        Curso cursoGuardado = cursoService.guardar(nuevoCurso);

        assertNotNull(cursoGuardado.getId(), "El ID del curso no debería ser nulo después de guardar por el servicio");
        assertEquals("Gestión de Proyectos Agile", cursoGuardado.getTitulo());
        
        // Verificar directamente en la base de datos (a través del repositorio)
        Optional<Curso> encontradoEnDB = cursoRepository.findById(cursoGuardado.getId());
        assertTrue(encontradoEnDB.isPresent());
        assertEquals("Gestión de Proyectos Agile", encontradoEnDB.get().getTitulo());
    }

    @Test
    void testService_VerTodosLosCursos() {
        cursoRepository.save(new Curso("Microservicios con Spring Boot", "Arquitectura", "Patrones de microservicios", "ArchitecPro", 200.0, 5.0));
        cursoRepository.save(new Curso("Patrones de Diseño", "Software", "Soluciones comunes de diseño", "Design Master", 100.0, 4.8));

        List<Curso> cursos = cursoService.verCursos(); 

        assertNotNull(cursos);
        assertEquals(2, cursos.size());
        assertTrue(cursos.stream().anyMatch(c -> c.getTitulo().equals("Microservicios con Spring Boot")));
        assertTrue(cursos.stream().anyMatch(c -> c.getTitulo().equals("Patrones de Diseño")));
    }

    @Test
    void testService_BuscarCursoPorIdExistente() {
        Curso cursoExistente = new Curso("DevOps con Jenkins", "DevOps", "CI/CD con Jenkins", "Pipeline Ace", 160.0, 4.9);
        cursoExistente = cursoRepository.save(cursoExistente); // Guardar para obtener el ID
        Optional<Curso> encontrado = cursoService.buscarCurso(cursoExistente.getId()); 
        assertTrue(encontrado.isPresent());
        assertEquals("DevOps con Jenkins", encontrado.get().getTitulo());
    }

    @Test
    void testService_BuscarCursoPorIdNoExistente() {
        Optional<Curso> encontrado = cursoService.buscarCurso(999L);
        assertFalse(encontrado.isPresent());
    }

    @Test
    void testService_ActualizarCursoExistente() {
        // Arrange
        Curso cursoOriginal = new Curso("Seguridad Web", "Desarrollo", "Protegiendo aplicaciones web", "Ms. Secure", 120.0, 4.6);
        cursoOriginal = cursoRepository.save(cursoOriginal); // Guardar para obtener el ID

        // Crear una instancia de Curso con los nuevos datos, como si viniera de una petición
        Curso datosActualizados = new Curso("Seguridad Web Avanzada", "Desarrollo", "Técnicas de hacking ético", "Ms. Secure", 150.0, 4.8);

        Curso cursoActualizado = cursoService.actualizarCurso(datosActualizados, cursoOriginal.getId());
        assertNotNull(cursoActualizado);
        assertEquals(cursoOriginal.getId(), cursoActualizado.getId()); // El ID debería ser el mismo
        assertEquals("Seguridad Web Avanzada", cursoActualizado.getTitulo());
        assertEquals(150.0, cursoActualizado.getPrice());
        assertEquals(4.8, cursoActualizado.getPopularidad());
        // Verificar en la DB
        Optional<Curso> verificadoEnDB = cursoRepository.findById(cursoOriginal.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("Seguridad Web Avanzada", verificadoEnDB.get().getTitulo());
    }

    @Test
    void testService_ActualizarCursoNoExistenteDebeLanzarNoSuchElementException() {
        Curso datosActualizados = new Curso("Curso Ficticio", "Ficción", "Esto no existe", "Nadie", 1.0, 1.0);
        Long idInexistente = 999L;
        assertThrows(java.util.NoSuchElementException.class, () -> {
            cursoService.actualizarCurso(datosActualizados, idInexistente);
        });

        // Asegurarse de que no se haya guardado nada nuevo en la DB
        assertEquals(0, cursoRepository.count());
    }

    @Test
    void testService_EliminarCursoExistente() {
        Curso cursoAEliminar = new Curso("Uso de Git y GitHub", "Herramientas", "Control de versiones para equipos", "Repo Master", 40.0, 4.1);
        cursoAEliminar = cursoRepository.save(cursoAEliminar); // Guardar para obtener el ID

        Boolean resultado = cursoService.eliminarPorId(cursoAEliminar.getId()); 

        assertTrue(resultado, "El curso debería haber sido eliminado exitosamente");
        Optional<Curso> eliminado = cursoRepository.findById(cursoAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El curso no debería estar en la base de datos después de eliminarlo");
    }

    @Test
    void testService_EliminarCursoNoExistente() {
        Long idInexistente = 999L;

        Boolean resultado = cursoService.eliminarPorId(idInexistente);

        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar un curso inexistente");
        // Asegurarse de que la base de datos sigue vacía o sin cambios
        assertEquals(0, cursoRepository.count());
    }
}