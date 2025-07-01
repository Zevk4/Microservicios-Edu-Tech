package com.microservices.microservicios.IntegrationTest;

import com.microservices.microservicios.MicroserviciosApplication;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.repository.UsuarioRepository;
import com.microservices.microservicios.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException; 
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MicroserviciosApplication.class)
@ActiveProfiles("test") 
@Transactional // Asegura que cada test se ejecute en una transacción y se haga rollback al final
public class UsuarioIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository; // Inyecta el repositorio real
    
    @Autowired
    private RolRepository rolRepository; // También necesitamos el repositorio de Rol para manejar relaciones

    @Autowired
    private UsuarioService usuarioService; // Inyecta el servicio real

    // Roles que necesitaremos para las pruebas
    private Rol rolEstudiante;
    private Rol rolAdmin;
    private Rol rolProfesor;

    @BeforeEach
    void setUp() {
        // Limpiar ambas tablas para asegurar un estado limpio
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        // Crear roles predefinidos que serán necesarios para los usuarios
        // Esto es crucial para que los tests de usuario pasen, ya que 'crearUsuario'
        // espera que el rol 'Estudiante' exista.
        rolEstudiante = rolRepository.save(new Rol("Estudiante"));
        rolAdmin = rolRepository.save(new Rol("Administrador"));
        rolProfesor = rolRepository.save(new Rol("Profesor"));
    }

    // --- Tests para la capa de Repositorio (validando JPA/Hibernate y mapeo) ---

    @Test
    void testRepository_GuardarNuevoUsuario() {
        // Crear un usuario con un rol predefinido (rolEstudiante se crea en BeforeEach)
        Usuario nuevoUsuario = new Usuario("Juan Perez", "juan.perez@example.com", "password123", rolEstudiante);

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        assertNotNull(usuarioGuardado.getId(), "El ID del usuario no debería ser nulo después de guardar");
        assertEquals("Juan Perez", usuarioGuardado.getNombre());
        assertEquals("juan.perez@example.com", usuarioGuardado.getEmail());
        assertEquals(rolEstudiante.getId(), usuarioGuardado.getRol().getId(), "El rol debe ser el asignado");

        Optional<Usuario> recuperado = usuarioRepository.findById(usuarioGuardado.getId());
        assertTrue(recuperado.isPresent());
        assertEquals("juan.perez@example.com", recuperado.get().getEmail());
        assertEquals(rolEstudiante.getNombre(), recuperado.get().getRol().getNombre());
    }

    @Test
    void testRepository_GuardarUsuarioConEmailDuplicado_DebeLanzarExcepcion() {
        usuarioRepository.save(new Usuario("Pedro", "pedro@example.com", "pass1", rolEstudiante));

        // Intentar guardar otro usuario con el mismo email
        assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioRepository.save(new Usuario("Ana", "pedro@example.com", "pass2", rolAdmin));
        }, "Debería lanzar DataIntegrityViolationException por email duplicado");
    }

    @Test
    void testRepository_BuscarUsuarioPorIdExistente() {
        Usuario usuarioExistente = usuarioRepository.save(new Usuario("Maria Gomez", "maria@example.com", "pass_maria", rolProfesor));
        Optional<Usuario> encontrado = usuarioRepository.findById(usuarioExistente.getId());
        assertTrue(encontrado.isPresent(), "El usuario debería ser encontrado por su ID");
        assertEquals("maria@example.com", encontrado.get().getEmail());
    }

    @Test
    void testRepository_BuscarUsuarioPorIdNoExistente() {
        Optional<Usuario> encontrado = usuarioRepository.findById(999L); // Un ID que no existe
        assertFalse(encontrado.isPresent(), "No se debería encontrar un usuario con un ID inexistente");
    }

    @Test
    void testRepository_BuscarUsuarioPorEmailExistente() {
        usuarioRepository.save(new Usuario("Carlos Ruiz", "carlos@example.com", "pass_carlos", rolEstudiante));
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("carlos@example.com");
        assertTrue(encontrado.isPresent(), "El usuario debería ser encontrado por su email");
        assertEquals("Carlos Ruiz", encontrado.get().getNombre());
    }

    @Test
    void testRepository_BuscarUsuarioPorEmailNoExistente() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("noexistente@example.com");
        assertFalse(encontrado.isPresent(), "No se debería encontrar un usuario con un email inexistente");
    }

    @Test
    void testRepository_ListarTodosLosUsuarios() {
        usuarioRepository.save(new Usuario("Lucia", "lucia@example.com", "passL", rolEstudiante));
        usuarioRepository.save(new Usuario("Roberto", "roberto@example.com", "passR", rolAdmin));

        List<Usuario> usuarios = usuarioRepository.findAll();

        assertNotNull(usuarios, "La lista de usuarios no debería ser nula");
        assertEquals(2, usuarios.size(), "Debería haber 2 usuarios en la lista");
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("lucia@example.com")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("roberto@example.com")));
    }

    @Test
    void testRepository_ActualizarUsuarioExistente() {
        Usuario usuarioOriginal = usuarioRepository.save(new Usuario("Original User", "original@example.com", "passOrig", rolEstudiante));

        usuarioOriginal.setNombre("Updated User");
        usuarioOriginal.setEmail("updated@example.com");
        usuarioOriginal.setRol(rolProfesor); // Cambiar el rol

        Usuario usuarioActualizado = usuarioRepository.save(usuarioOriginal);

        assertNotNull(usuarioActualizado);
        assertEquals(usuarioOriginal.getId(), usuarioActualizado.getId());
        assertEquals("Updated User", usuarioActualizado.getNombre());
        assertEquals("updated@example.com", usuarioActualizado.getEmail());
        assertEquals(rolProfesor.getId(), usuarioActualizado.getRol().getId());
        assertEquals("Profesor", usuarioActualizado.getRol().getNombre());
    }

    @Test
    void testRepository_EliminarUsuarioExistente() {
        Usuario usuarioAEliminar = usuarioRepository.save(new Usuario("To Delete", "todelete@example.com", "delete_pass", rolEstudiante));

        usuarioRepository.deleteById(usuarioAEliminar.getId());

        Optional<Usuario> eliminado = usuarioRepository.findById(usuarioAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El usuario debería haber sido eliminado");
    }

    // --- Tests para la capa de Servicio (validando la interacción con repositorios y la DB) ---

    @Test
    void testService_CrearUsuarioExitoso() {
        Usuario nuevoUsuario = new Usuario("Fernando", "fernando@example.com", "passFer", null); // Rol se asigna en el servicio
        Usuario usuarioCreado = usuarioService.crearUsuario(nuevoUsuario);

        assertNotNull(usuarioCreado.getId());
        assertEquals("Fernando", usuarioCreado.getNombre());
        assertEquals("fernando@example.com", usuarioCreado.getEmail());
        // El servicio asigna el rol "Estudiante"
        assertEquals(rolEstudiante.getId(), usuarioCreado.getRol().getId());
        assertEquals("Estudiante", usuarioCreado.getRol().getNombre());

        // Verificar en la DB a través del repositorio
        Optional<Usuario> encontradoEnDB = usuarioRepository.findById(usuarioCreado.getId());
        assertTrue(encontradoEnDB.isPresent());
        assertEquals("fernando@example.com", encontradoEnDB.get().getEmail());
    }

    @Test
    void testService_CrearUsuario_RolEstudianteNoExistente_LanzaRuntimeException() {
        // Limpiamos los roles para simular que 'Estudiante' no existe
        rolRepository.deleteAll();
        // Intentamos crear un usuario
        Usuario nuevoUsuario = new Usuario("Faulty User", "faulty@example.com", "faulty_pass", null);

        // Esperamos que el servicio lance una RuntimeException
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(nuevoUsuario);
        });

        assertEquals("Rol 'Estudiante' no encontrado en la base de datos.", thrown.getMessage());
        // Asegurarse de que no se haya guardado ningún usuario
        assertEquals(0, usuarioRepository.count());
    }

    @Test
    void testService_GetUsuarios() {
        usuarioRepository.save(new Usuario("User A", "userA@example.com", "passA", rolEstudiante));
        usuarioRepository.save(new Usuario("User B", "userB@example.com", "passB", rolAdmin));
        List<Usuario> usuarios = usuarioService.getUsuarios();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("userA@example.com")));
        assertTrue(usuarios.stream().anyMatch(u -> u.getEmail().equals("userB@example.com")));
    }

    @Test
    void testService_GetByIdExistente() {
        Usuario usuarioExistente = usuarioRepository.save(new Usuario("User C", "userC@example.com", "passC", rolProfesor));
        Optional<Usuario> encontrado = usuarioService.getById(usuarioExistente.getId());
        assertTrue(encontrado.isPresent());
        assertEquals("userC@example.com", encontrado.get().getEmail());
    }

    @Test
    void testService_GetByIdNoExistente() {
        Optional<Usuario> encontrado = usuarioService.getById(999L);
        assertFalse(encontrado.isPresent());
    }

    @Test
    void testService_UpdateByIdExistente() {
        Usuario usuarioOriginal = usuarioRepository.save(new Usuario("Original Name", "original@example.com", "orig_pass", rolEstudiante));
        // Datos para actualizar, incluyendo un nuevo rol
        Usuario datosActualizados = new Usuario("Updated Name", "updated@example.com", "new_pass", rolAdmin); // Este rol será el que se use
        Usuario usuarioActualizado = usuarioService.updateById(datosActualizados, usuarioOriginal.getId());

        assertNotNull(usuarioActualizado);
        assertEquals(usuarioOriginal.getId(), usuarioActualizado.getId());
        assertEquals("Updated Name", usuarioActualizado.getNombre());
        assertEquals("updated@example.com", usuarioActualizado.getEmail());
        assertEquals("new_pass", usuarioActualizado.getPassword());
        assertEquals(rolAdmin.getId(), usuarioActualizado.getRol().getId()); // Verifica que el rol fue actualizado
        assertEquals("Administrador", usuarioActualizado.getRol().getNombre());

        // Verificar en la DB
        Optional<Usuario> verificadoEnDB = usuarioRepository.findById(usuarioOriginal.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("updated@example.com", verificadoEnDB.get().getEmail());
        assertEquals("Administrador", verificadoEnDB.get().getRol().getNombre());
    }

    @Test
    void testService_UpdateByIdNoExistenteDebeLanzarNoSuchElementException() {
        Usuario datosActualizados = new Usuario("NonExistent", "nonexistent@example.com", "pass", rolEstudiante);
        Long idInexistente = 999L;
        assertThrows(NoSuchElementException.class, () -> {
            usuarioService.updateById(datosActualizados, idInexistente);
        });

        // Asegurarse de que no se haya guardado nada nuevo
        assertEquals(3, rolRepository.count()); // Los roles pre-creados siguen ahí
        assertEquals(0, usuarioRepository.count()); // No se crearon usuarios
    }

    @Test
    void testService_DeleteByIdExistente() {
        Usuario usuarioAEliminar = usuarioRepository.save(new Usuario("Delete Me", "delete@example.com", "passD", rolEstudiante));
        Boolean resultado = usuarioService.deleteById(usuarioAEliminar.getId());
        assertTrue(resultado, "El usuario debería haber sido eliminado exitosamente");
        Optional<Usuario> eliminado = usuarioRepository.findById(usuarioAEliminar.getId());
        assertFalse(eliminado.isPresent(), "El usuario no debería estar en la base de datos después de eliminarlo");
    }

    @Test
    void testService_DeleteByIdNoExistente() {
        Long idInexistente = 999L;
        Boolean resultado = usuarioService.deleteById(idInexistente);
        assertFalse(resultado, "El servicio debería indicar que no se pudo eliminar un usuario inexistente");
        // Asegurarse de que no se afectó a otros usuarios o roles
        assertEquals(0, usuarioRepository.count());
        assertEquals(3, rolRepository.count());
    }

    @Test
    void testService_ChangeRolExitoso() {
        Usuario usuarioParaCambiarRol = usuarioRepository.save(new Usuario("Change Rol", "changerol@example.com", "cr_pass", rolEstudiante));
        Usuario usuarioConRolCambiado = usuarioService.changeRol(usuarioParaCambiarRol.getId(), "Administrador");
        assertNotNull(usuarioConRolCambiado);
        assertEquals(usuarioParaCambiarRol.getId(), usuarioConRolCambiado.getId());
        assertEquals("Administrador", usuarioConRolCambiado.getRol().getNombre());
        assertEquals(rolAdmin.getId(), usuarioConRolCambiado.getRol().getId());
        // Verificar en la DB
        Optional<Usuario> verificadoEnDB = usuarioRepository.findById(usuarioParaCambiarRol.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("Administrador", verificadoEnDB.get().getRol().getNombre());
    }

    @Test
    void testService_ChangeRol_UsuarioNoExistente_LanzaRuntimeException() {
        Long idInexistente = 999L;
        String newRoleName = "Administrador";
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.changeRol(idInexistente, newRoleName);
        });

        assertEquals("Usuario con ID " + idInexistente + " no encontrado.", thrown.getMessage());
    }

    @Test
    void testService_ChangeRol_RolNoExistente_LanzaRuntimeException() {
        Usuario usuarioExistente = usuarioRepository.save(new Usuario("User With Role", "userrole@example.com", "user_pass", rolEstudiante));
        String newRoleName = "RolInexistente";
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.changeRol(usuarioExistente.getId(), newRoleName);
        });

        assertEquals("Rol '" + newRoleName + "' no encontrado en la base de datos.", thrown.getMessage());
        // Asegurarse de que el rol del usuario no cambió en la DB
        Optional<Usuario> verificadoEnDB = usuarioRepository.findById(usuarioExistente.getId());
        assertTrue(verificadoEnDB.isPresent());
        assertEquals("Estudiante", verificadoEnDB.get().getRol().getNombre());
    }
}