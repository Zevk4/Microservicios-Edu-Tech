package com.microservices.microservicios.ServiceTest;

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.repository.UsuarioRepository;
import com.microservices.microservicios.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock // Mock del repositorio de Usuario
    private UsuarioRepository userRepo;

    @Mock // Mock del repositorio de Rol
    private RolRepository rolRepo;

    @InjectMocks // Inyecta los mocks en la instancia de UsuarioService
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    // Método de utilidad para crear un Rol mock
    private Rol createMockRol(Long id, String nombre) {
        Rol rol = new Rol(nombre);
        rol.setId(id);
        return rol;
    }

    @Test
    void testGetUsuarios() {
        // Arrange
        Rol rolAdmin = createMockRol(1L, "ADMIN");
        Rol rolUser = createMockRol(2L, "USER");

        Usuario user1 = new Usuario("Juan", "juan@mail.com", "pass1", rolAdmin);
        user1.setId(10L);
        Usuario user2 = new Usuario("Maria", "maria@mail.com", "pass2", rolUser);
        user2.setId(11L);

        ArrayList<Usuario> usuarios = new ArrayList<>(Arrays.asList(user1, user2));

        // Configura el mock para que cuando se llame a findAll en userRepo, devuelva la lista de usuarios
        when(userRepo.findAll()).thenReturn(usuarios);

        // Act
        ArrayList<Usuario> resultado = usuarioService.getUsuarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        assertEquals("maria@mail.com", resultado.get(1).getEmail());
        assertEquals("ADMIN", resultado.get(0).getRol().getNombre());
        // Verifica que el método findAll del userRepo fue llamado una vez
        verify(userRepo, times(1)).findAll();
    }

    @Test
    void testCrearUsuarioExitoso() {
        // Arrange
        Rol rolEstudiante = createMockRol(3L, "Estudiante");
        Usuario usuarioEntrada = new Usuario("Nuevo Usuario", "nuevo@mail.com", "newpass", null); // El rol se asigna en el servicio

        Usuario usuarioGuardado = new Usuario("Nuevo Usuario", "nuevo@mail.com", "newpass", rolEstudiante);
        usuarioGuardado.setId(1L);

        // Configura mocks:
        // 1. Cuando se busque el rol "Estudiante", devolverlo.
        when(rolRepo.findByNombre("Estudiante")).thenReturn(Optional.of(rolEstudiante));
        // 2. Cuando se guarde cualquier Usuario, devolver el usuario simulado con ID
        when(userRepo.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // Act
        Usuario resultado = usuarioService.crearUsuario(usuarioEntrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Nuevo Usuario", resultado.getNombre());
        assertEquals("Estudiante", resultado.getRol().getNombre());
        // Verifica que findByNombre de rolRepo fue llamado una vez con "Estudiante"
        verify(rolRepo, times(1)).findByNombre("Estudiante");
        // Verifica que save de userRepo fue llamado una vez con cualquier Usuario
        verify(userRepo, times(1)).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuarioCuandoRolEstudianteNoExisteDebeLanzarExcepcion() {
        // Arrange
        Usuario usuarioEntrada = new Usuario("Fallido", "fallido@mail.com", "failpass", null);

        // Configura mock: Cuando se busque el rol "Estudiante", devolver Optional vacío
        when(rolRepo.findByNombre("Estudiante")).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica que se lanza una RuntimeException con el mensaje esperado
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(usuarioEntrada);
        });

        assertEquals("Rol 'Estudiante' no encontrado en la base de datos.", thrown.getMessage());
        // Verifica que findByNombre fue llamado una vez
        verify(rolRepo, times(1)).findByNombre("Estudiante");
        // Asegura que save nunca fue llamado en userRepo
        verify(userRepo, never()).save(any(Usuario.class));
    }

    @Test
    void testGetByIdExistente() {
        // Arrange
        Rol rolUser = createMockRol(2L, "USER");
        Usuario usuario = new Usuario("Pedro", "pedro@mail.com", "pedropass", rolUser);
        usuario.setId(5L);

        // Configura el mock para que cuando se llame a findById con 5L, devuelva el usuario
        when(userRepo.findById(5L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.getById(5L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(5L, resultado.get().getId());
        assertEquals("Pedro", resultado.get().getNombre());
        // Verifica que findById de userRepo fue llamado una vez
        verify(userRepo, times(1)).findById(5L);
    }

    @Test
    void testGetByIdNoExistente() {
        // Arrange
        // Configura el mock para que findById devuelva un Optional vacío
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.getById(99L);

        // Assert
        assertFalse(resultado.isPresent());
        // Verifica que findById de userRepo fue llamado una vez
        verify(userRepo, times(1)).findById(99L);
    }

    @Test
    void testUpdateByIdExistente() {
        // Arrange
        Rol rolAdmin = createMockRol(1L, "ADMIN");
        Usuario usuarioExistente = new Usuario("Original", "original@mail.com", "origpass", rolAdmin);
        usuarioExistente.setId(1L);

        Usuario usuarioActualizadoData = new Usuario("Modificado", "modificado@mail.com", "modpass", rolAdmin);
        // El ID no se establece aquí, ya que el servicio lo obtiene del existente

        // Configura mocks:
        // 1. Cuando se busque por ID, devolver el usuario existente.
        when(userRepo.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        // 2. Cuando se guarde la instancia modificada, devolver esa misma instancia.
        when(userRepo.save(any(Usuario.class))).thenReturn(usuarioExistente);

        // Act
        Usuario resultado = usuarioService.updateById(usuarioActualizadoData, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId()); // El ID debería ser el mismo
        assertEquals("Modificado", resultado.getNombre());
        assertEquals("modificado@mail.com", resultado.getEmail());
        assertEquals("modpass", resultado.getPassword()); // Verifica la actualización del password
        assertEquals("ADMIN", resultado.getRol().getNombre());
        // Verifica que findById y save fueron llamados una vez
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(usuarioExistente); // Verifica que se guarda la instancia existente modificada
    }

    @Test
    void testUpdateByIdNoExistenteDebeLanzarExcepcion() {
        // Arrange
        Usuario usuarioParaActualizar = new Usuario("Inexistente", "inexistente@mail.com", "dummy", null);

        // Configura el mock para que findById devuelva Optional vacío
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica que NoSuchElementException es lanzada por el .get() en tu servicio
        assertThrows(java.util.NoSuchElementException.class, () -> {
            usuarioService.updateById(usuarioParaActualizar, 99L);
        });

        // Verifica que findById fue llamado una vez
        verify(userRepo, times(1)).findById(99L);
        // Asegura que save nunca fue llamado
        verify(userRepo, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarPorIdExistente() {
        // Arrange
        Long idExistente = 1L;
        // Mock: Cuando el servicio pregunte si existe, devuelve true
        when(userRepo.existsById(idExistente)).thenReturn(true);
        // Mock: Cuando el servicio pida eliminar, no hagas nada (simula éxito)
        doNothing().when(userRepo).deleteById(idExistente);

        // Act
        Boolean resultado = usuarioService.deleteById(idExistente);

        // Assert
        assertTrue(resultado, "Debería retornar true si el curso existe y es eliminado");
        // Verifica que existsById fue llamado una vez
        verify(userRepo, times(1)).existsById(idExistente);
        // Verifica que deleteById fue llamado una vez
        verify(userRepo, times(1)).deleteById(idExistente);
    }
    @Test
    void testEliminarPorIdNoExistente() {
        // Arrange
        Long idInexistente = 99L;
        // Mock: Cuando el servicio pregunte si existe, devuelve false
        when(userRepo.existsById(idInexistente)).thenReturn(false);

        // Act
        Boolean resultado = usuarioService.deleteById(idInexistente);

        // Assert
        assertFalse(resultado, "Debería retornar false si el curso no existe");
        // Verifica que existsById fue llamado una vez
        verify(userRepo, times(1)).existsById(idInexistente);
        // Verifica que deleteById NUNCA fue llamado (porque el curso no existía)
        verify(userRepo, never()).deleteById(anyLong()); 
    }

    @Test
    void testChangeRolExitoso() {
        // Arrange
        Rol rolUser = createMockRol(2L, "USER");
        Rol rolAdmin = createMockRol(1L, "ADMIN");

        Usuario existingUser = new Usuario("Carlos", "carlos@mail.com", "carlospass", rolUser);
        existingUser.setId(100L);

        Usuario updatedUser = new Usuario("Carlos", "carlos@mail.com", "carlospass", rolAdmin);
        updatedUser.setId(100L);

        // Configura mocks:
        // 1. Cuando se busque el usuario por ID, devolverlo.
        when(userRepo.findById(100L)).thenReturn(Optional.of(existingUser));
        // 2. Cuando se busque el rol "ADMIN", devolverlo.
        when(rolRepo.findByNombre("ADMIN")).thenReturn(Optional.of(rolAdmin));
        // 3. Cuando se guarde el usuario, devolver el usuario actualizado.
        when(userRepo.save(any(Usuario.class))).thenReturn(updatedUser);

        // Act
        Usuario resultado = usuarioService.changeRol(100L, "ADMIN");

        // Assert
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("ADMIN", resultado.getRol().getNombre()); // Verifica que el rol fue cambiado
        // Verifica las interacciones con los repositorios
        verify(userRepo, times(1)).findById(100L);
        verify(rolRepo, times(1)).findByNombre("ADMIN");
        verify(userRepo, times(1)).save(existingUser); // save se llama con la instancia existente modificada
    }

    @Test
    void testChangeRolUsuarioNoExistenteDebeLanzarExcepcion() {
        // Arrange
        // Cuando se busque el usuario, devolver Optional vacío
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica que se lanza RuntimeException con el mensaje específico
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.changeRol(999L, "EDITOR");
        });

        assertEquals("Usuario con ID 999 no encontrado.", thrown.getMessage());
        // Verifica que findById fue llamado
        verify(userRepo, times(1)).findById(999L);
        // Asegura que no se interactuó con rolRepo ni se guardó nada
        verify(rolRepo, never()).findByNombre(anyString());
        verify(userRepo, never()).save(any(Usuario.class));
    }

    @Test
    void testChangeRolNuevoRolNoExistenteDebeLanzarExcepcion() {
        // Arrange
        Rol rolUser = createMockRol(2L, "USER");
        Usuario existingUser = new Usuario("Carlos", "carlos@mail.com", "carlospass", rolUser);
        existingUser.setId(100L);

        // 1. Cuando se busque el usuario, devolverlo.
        when(userRepo.findById(100L)).thenReturn(Optional.of(existingUser));
        // 2. Cuando se busque el nuevo rol, devolver Optional vacío.
        when(rolRepo.findByNombre("ROL_INEXISTENTE")).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica que se lanza RuntimeException con el mensaje específico
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.changeRol(100L, "ROL_INEXISTENTE");
        });

        assertEquals("Rol 'ROL_INEXISTENTE' no encontrado en la base de datos.", thrown.getMessage());
        // Verifica que findById de usuario y findByNombre de rol fueron llamados
        verify(userRepo, times(1)).findById(100L);
        verify(rolRepo, times(1)).findByNombre("ROL_INEXISTENTE");
        // Asegura que save nunca fue llamado
        verify(userRepo, never()).save(any(Usuario.class));
    }
}