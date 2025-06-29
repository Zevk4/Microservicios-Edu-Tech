package com.microservices.microservicios.IntegrationTest;

import com.microservices.microservicios.MicroserviciosApplication;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.service.RolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException; // Para Unique Constraint
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MicroserviciosApplication.class)
@ActiveProfiles("test") //Configuración para el perfil 'test' (ej. H2)
@Transactional // Cada test se ejecuta en su propia transacción y se hace rollback
public class RolIntegrationTest {

    @Autowired
    private RolRepository rolRepository; // Inyecta el repositorio real

    @Autowired
    private RolService rolService; // Inyecta el servicio real

    @BeforeEach
    void setUp() {
        // Limpiar la base de datos antes de cada test para asegurar un estado limpio
        rolRepository.deleteAll();
    }

    // --- Tests para la capa de Repositorio (validando JPA/Hibernate y mapeo) ---

    @Test
    void testRepository_GuardarNuevoRol() {
        // Arrange
        Rol nuevoRol = new Rol("ADMIN");

        // Act
        Rol rolGuardado = rolRepository.save(nuevoRol);

        // Assert
        assertNotNull(rolGuardado.getId(), "El ID del rol no debería ser nulo después de guardar");
        assertEquals("ADMIN", rolGuardado.getNombre());

        // Verificar que el rol se puede recuperar por ID
        Optional<Rol> recuperado = rolRepository.findById(rolGuardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals("ADMIN", recuperado.get().getNombre());
    }

    @Test
    void testRepository_GuardarRolConNombreDuplicado_DebeLanzarExcepcion() {
        // Arrange
        rolRepository.save(new Rol("USER")); // Guarda un rol con nombre "USER"

        // Act & Assert
        // Intentar guardar otro rol con el mismo nombre "USER"
        assertThrows(DataIntegrityViolationException.class, () -> {
            rolRepository.save(new Rol("USER"));
        }, "Debería lanzar DataIntegrityViolationException por nombre duplicado");
    }

    @Test
    void testRepository_BuscarRolPorIdExistente() {
        // Arrange
        Rol rolExistente = rolRepository.save(new Rol("EDITOR"));

        // Act
        Optional<Rol> encontrado = rolRepository.findById(rolExistente.getId());

        // Assert
        assertTrue(encontrado.isPresent(), "El rol debería ser encontrado por su ID");
        assertEquals("EDITOR", encontrado.get().getNombre());
    }

    @Test
    void testRepository_BuscarRolPorIdNoExistente() {
        // Act
        Optional<Rol> encontrado = rolRepository.findById(999L); // Un ID que no existe

        // Assert
        assertFalse(encontrado.isPresent(), "No se debería encontrar un rol con un ID inexistente");
    }

    @Test
    void testRepository_BuscarRolPorNombreExistente() {
        // Arrange
        rolRepository.save(new Rol("INVITADO"));

        // Act
        Optional<Rol> encontrado = rolRepository.findByNombre("INVITADO");

        // Assert
        assertTrue(encontrado.isPresent(), "El rol debería ser encontrado por su nombre");
        assertEquals("INVITADO", encontrado.get().getNombre());
    }

    @Test
    void testRepository_BuscarRolPorNombreNoExistente() {
        // Act
        Optional<Rol> encontrado = rolRepository.findByNombre("SUPER_ADMIN");

        // Assert
        assertFalse(encontrado.isPresent(), "No se debería encontrar un rol con un nombre inexistente");
    }

    @Test
    void testRepository_ListarTodosLosRoles() {
        // Arrange
        rolRepository.save(new Rol("GESTOR"));
        rolRepository.save(new Rol("AUDITOR"));

        // Act
        List<Rol> roles = rolRepository.findAll();

        // Assert
        assertNotNull(roles, "La lista de roles no debería ser nula");
        assertEquals(2, roles.size(), "Debería haber 2 roles en la lista");
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("GESTOR")));
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("AUDITOR")));
    }

    @Test
    void testRepository_ActualizarRolExistente() {
        // Arrange
        Rol rolOriginal = rolRepository.save(new Rol("VIEJO_ROL"));

        // Act
        rolOriginal.setNombre("NUEVO_ROL");
        Rol rolActualizado = rolRepository.save(rolOriginal); // Vuelve a guardar el objeto modificado

        // Assert
        assertNotNull(rolActualizado);
        assertEquals(rolOriginal.getId(), rolActualizado.getId());
        assertEquals("NUEVO_ROL", rolActualizado.getNombre());
        
        // Verificar que el cambio se ha persistido
        Optional<Rol> verificado = rolRepository.findById(rolActualizado.getId());
        assertTrue(verificado.isPresent());
        assertEquals("NUEVO_ROL", verificado.get().getNombre());
    }

    @Test
    void testRepository_EliminarRolExistente() {
        // Arrange
        Rol rolAEliminar = rolRepository.save(new Rol("ROL_TEMPORAL"));

        // Act
        rolRepository.deleteById(rolAEliminar.getId());

        // Assert
        Optional<Rol> eliminado = rolRepository.findById(rolAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El rol debería haber sido eliminado");
    }

    // --- Tests para la capa de Servicio (validando la interacción con el repositorio y la DB) ---

    @Test
    void testService_GuardarNuevoRol() {
        // Arrange
        Rol nuevoRol = new Rol("LECTOR");

        // Act
        Rol rolGuardado = rolService.guardar(nuevoRol);

        // Assert
        assertNotNull(rolGuardado.getId(), "El ID del rol no debería ser nulo después de guardar por el servicio");
        assertEquals("LECTOR", rolGuardado.getNombre());
        
        // Verificar directamente en la base de datos (a través del repositorio)
        Optional<Rol> encontradoEnDB = rolRepository.findById(rolGuardado.getId());
        assertTrue(encontradoEnDB.isPresent());
        assertEquals("LECTOR", encontradoEnDB.get().getNombre());
    }

    @Test
    void testService_VerTodosLosRoles() {
        // Arrange
        rolRepository.save(new Rol("CONTABLE"));
        rolRepository.save(new Rol("RRHH"));

        // Act
        List<Rol> roles = rolService.verRoles();

        // Assert
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("CONTABLE")));
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("RRHH")));
    }

    @Test
    void testService_BuscarRolPorIdExistente() {
        // Arrange
        Rol rolExistente = rolRepository.save(new Rol("CONSULTOR"));

        // Act
        Optional<Rol> encontrado = rolService.buscarRoles(rolExistente.getId()); // Tu servicio tiene 'buscarRoles'

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("CONSULTOR", encontrado.get().getNombre());
    }

    @Test
    void testService_BuscarRolPorIdNoExistente() {
        // Act
        Optional<Rol> encontrado = rolService.buscarRoles(999L);

        // Assert
        assertFalse(encontrado.isPresent());
    }

    @Test
    void testService_ActualizarRolExistente() {
        // Arrange
        Rol rolOriginal = rolRepository.save(new Rol("ROL_ORIGINAL_SRV"));

        // Crear una instancia de Rol con los nuevos datos
        Rol datosActualizados = new Rol("ROL_ACTUALIZADO_SRV");

        // Act
        Rol rolActualizado = rolService.actualizarRol(datosActualizados, rolOriginal.getId());

        // Assert
        assertNotNull(rolActualizado);
        assertEquals(rolOriginal.getId(), rolActualizado.getId()); // El ID debería ser el mismo
        assertEquals("ROL_ACTUALIZADO_SRV", rolActualizado.getNombre());

        // Verificar en la DB directamente
        Optional<Rol> verificadoEnDB = rolRepository.findById(rolOriginal.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("ROL_ACTUALIZADO_SRV", verificadoEnDB.get().getNombre());
    }

    @Test
    void testService_ActualizarRolNoExistenteDebeLanzarNoSuchElementException() {
        // Arrange
        Rol datosActualizados = new Rol("ROL_FICTICIO");
        Long idInexistente = 999L;

        // Act & Assert
        // Basado en tu RolService original: findById(id).get() lanza NoSuchElementException si no encuentra
        assertThrows(java.util.NoSuchElementException.class, () -> {
            rolService.actualizarRol(datosActualizados, idInexistente);
        });

        // Asegurarse de que no se haya guardado nada nuevo en la DB
        assertEquals(0, rolRepository.count());
    }

    @Test
    void testService_EliminarRolExistente() {
        // Arrange
        Rol rolAEliminar = rolRepository.save(new Rol("ROL_A_ELIMINAR"));

        // Act
        Boolean resultado = rolService.eliminarRolId(rolAEliminar.getId()); // Tu servicio usa 'eliminarRolId'

        // Assert
        assertTrue(resultado, "El rol debería haber sido eliminado exitosamente");
        Optional<Rol> eliminado = rolRepository.findById(rolAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El rol no debería estar en la base de datos después de eliminarlo");
    }

    @Test
    void testService_EliminarRolNoExistente() {
        // Arrange
        Long idInexistente = 999L;

        // Act
        Boolean resultado = rolService.eliminarRolId(idInexistente);

        // Assert
        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar un rol inexistente");
        // Asegurarse de que la base de datos sigue vacía o sin cambios
        assertEquals(0, rolRepository.count());
    }
}