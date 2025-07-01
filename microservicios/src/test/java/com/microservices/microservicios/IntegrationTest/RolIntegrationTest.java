package com.microservices.microservicios.IntegrationTest;

import com.microservices.microservicios.MicroserviciosApplication;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.service.RolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MicroserviciosApplication.class)
@ActiveProfiles("test") 
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
        Rol nuevoRol = new Rol("ADMIN");
        Rol rolGuardado = rolRepository.save(nuevoRol);

        assertNotNull(rolGuardado.getId(), "El ID del rol no debería ser nulo después de guardar");
        assertEquals("ADMIN", rolGuardado.getNombre());

        // Verificar que el rol se puede recuperar por ID
        Optional<Rol> recuperado = rolRepository.findById(rolGuardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals("ADMIN", recuperado.get().getNombre());
    }

    @Test
    void testRepository_GuardarRolConNombreDuplicado_DebeLanzarExcepcion() {
        rolRepository.save(new Rol("USER")); // Guarda un rol con nombre "USER"

        // Intentar guardar otro rol con el mismo nombre "USER"
        assertThrows(DataIntegrityViolationException.class, () -> {
            rolRepository.save(new Rol("USER"));
        }, "Debería lanzar DataIntegrityViolationException por nombre duplicado");
    }

    @Test
    void testRepository_BuscarRolPorIdExistente() {
        Rol rolExistente = rolRepository.save(new Rol("EDITOR"));

        Optional<Rol> encontrado = rolRepository.findById(rolExistente.getId());

        assertTrue(encontrado.isPresent(), "El rol debería ser encontrado por su ID");
        assertEquals("EDITOR", encontrado.get().getNombre());
    }

    @Test
    void testRepository_BuscarRolPorIdNoExistente() {
        Optional<Rol> encontrado = rolRepository.findById(999L); // Un ID que no existe
        assertFalse(encontrado.isPresent(), "No se debería encontrar un rol con un ID inexistente");
    }

    @Test
    void testRepository_BuscarRolPorNombreExistente() {
        rolRepository.save(new Rol("INVITADO"));
        Optional<Rol> encontrado = rolRepository.findByNombre("INVITADO");
        assertTrue(encontrado.isPresent(), "El rol debería ser encontrado por su nombre");
        assertEquals("INVITADO", encontrado.get().getNombre());
    }

    @Test
    void testRepository_BuscarRolPorNombreNoExistente() {
        Optional<Rol> encontrado = rolRepository.findByNombre("SUPER_ADMIN");
        assertFalse(encontrado.isPresent(), "No se debería encontrar un rol con un nombre inexistente");
    }

    @Test
    void testRepository_ListarTodosLosRoles() {
        rolRepository.save(new Rol("GESTOR"));
        rolRepository.save(new Rol("AUDITOR"));

        List<Rol> roles = rolRepository.findAll();
        
        assertNotNull(roles, "La lista de roles no debería ser nula");
        assertEquals(2, roles.size(), "Debería haber 2 roles en la lista");
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("GESTOR")));
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("AUDITOR")));
    }

    @Test
    void testRepository_ActualizarRolExistente() {
        Rol rolOriginal = rolRepository.save(new Rol("VIEJO_ROL"));
        rolOriginal.setNombre("NUEVO_ROL");
        Rol rolActualizado = rolRepository.save(rolOriginal); // Vuelve a guardar el objeto modificado

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
        Rol rolAEliminar = rolRepository.save(new Rol("ROL_TEMPORAL"));
        rolRepository.deleteById(rolAEliminar.getId());
        Optional<Rol> eliminado = rolRepository.findById(rolAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El rol debería haber sido eliminado");
    }

    // --- Tests para la capa de Servicio (validando la interacción con el repositorio y la DB) ---

    @Test
    void testService_GuardarNuevoRol() {
        Rol nuevoRol = new Rol("LECTOR");
        Rol rolGuardado = rolService.guardar(nuevoRol);
        assertNotNull(rolGuardado.getId(), "El ID del rol no debería ser nulo después de guardar por el servicio");
        assertEquals("LECTOR", rolGuardado.getNombre());
        
        // Verificar directamente en la base de datos (a través del repositorio)
        Optional<Rol> encontradoEnDB = rolRepository.findById(rolGuardado.getId());
        assertTrue(encontradoEnDB.isPresent());
        assertEquals("LECTOR", encontradoEnDB.get().getNombre());
    }

    @Test
    void testService_VerTodosLosRoles() {
        rolRepository.save(new Rol("CONTABLE"));
        rolRepository.save(new Rol("RRHH"));

        List<Rol> roles = rolService.verRoles();

        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("CONTABLE")));
        assertTrue(roles.stream().anyMatch(r -> r.getNombre().equals("RRHH")));
    }

    @Test
    void testService_BuscarRolPorIdExistente() {
        Rol rolExistente = rolRepository.save(new Rol("CONSULTOR"));

        Optional<Rol> encontrado = rolService.buscarRoles(rolExistente.getId()); 

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
        Rol rolOriginal = rolRepository.save(new Rol("ROL_ORIGINAL_SRV"));

        // Crear una instancia de Rol con los nuevos datos
        Rol datosActualizados = new Rol("ROL_ACTUALIZADO_SRV");
        Rol rolActualizado = rolService.actualizarRol(datosActualizados, rolOriginal.getId());

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
        Rol datosActualizados = new Rol("ROL_FICTICIO");
        Long idInexistente = 999L;
        assertThrows(java.util.NoSuchElementException.class, () -> {
            rolService.actualizarRol(datosActualizados, idInexistente);
        });
        assertEquals(0, rolRepository.count());
    }

    @Test
    void testService_EliminarRolExistente() {
        Rol rolAEliminar = rolRepository.save(new Rol("ROL_A_ELIMINAR"));
        Boolean resultado = rolService.eliminarRolId(rolAEliminar.getId());

        assertTrue(resultado, "El rol debería haber sido eliminado exitosamente");
        Optional<Rol> eliminado = rolRepository.findById(rolAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El rol no debería estar en la base de datos después de eliminarlo");
    }

    @Test
    void testService_EliminarRolNoExistente() {
        Long idInexistente = 999L;
        Boolean resultado = rolService.eliminarRolId(idInexistente);
        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar un rol inexistente");
        // Asegurarse de que la base de datos sigue vacía o sin cambios
        assertEquals(0, rolRepository.count());
    }
}