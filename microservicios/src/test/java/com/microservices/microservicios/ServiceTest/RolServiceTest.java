package com.microservices.microservicios.ServiceTest;

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.service.RolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RolServiceTest {

    @Mock // Mock del repositorio de Rol
    private RolRepository rolRepo;

    @InjectMocks // Inyecta los mocks en la instancia de RolService
    private RolService rolService;

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarRol() {
        // Arrange
        Rol rolEntrada = new Rol("EDITOR");
        Rol rolGuardado = new Rol("EDITOR");
        rolGuardado.setId(1L); // Simula que el repositorio le asigna un ID

        // Configura el mock para que cuando se llame a save, devuelva rolGuardado
        when(rolRepo.save(any(Rol.class))).thenReturn(rolGuardado);

        // Act
        Rol resultado = rolService.guardar(rolEntrada);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("EDITOR", resultado.getNombre());
        // Verifica que el método save del repositorio fue llamado exactamente una vez con cualquier objeto Rol
        verify(rolRepo, times(1)).save(any(Rol.class));
    }

    @Test
    void testVerRoles() {
        // Arrange
        Rol rol1 = new Rol("ADMIN");
        rol1.setId(1L);
        Rol rol2 = new Rol("USER");
        rol2.setId(2L);

        ArrayList<Rol> roles = new ArrayList<>(Arrays.asList(rol1, rol2));

        // Configura el mock para que cuando se llame a findAll, devuelva la lista de roles
        when(rolRepo.findAll()).thenReturn(roles);

        // Act
        ArrayList<Rol> resultado = rolService.verRoles();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ADMIN", resultado.get(0).getNombre());
        assertEquals("USER", resultado.get(1).getNombre());
        // Verifica que el método findAll del repositorio fue llamado exactamente una vez
        verify(rolRepo, times(1)).findAll();
    }

    @Test
    void testBuscarRolesExistente() {
        // Arrange
        Rol rol = new Rol("SUPERVISOR");
        rol.setId(3L);

        // Configura el mock para que cuando se llame a findById con 3L, devuelva el rol
        when(rolRepo.findById(3L)).thenReturn(Optional.of(rol));

        // Act
        Optional<Rol> resultado = rolService.buscarRoles(3L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(3L, resultado.get().getId());
        assertEquals("SUPERVISOR", resultado.get().getNombre());
        // Verifica que el método findById del repositorio fue llamado exactamente una vez con 3L
        verify(rolRepo, times(1)).findById(3L);
    }

    @Test
    void testBuscarRolesNoExistente() {
        // Arrange
        // Configura el mock para que cuando se llame a findById con cualquier Long, devuelva un Optional vacío
        when(rolRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Rol> resultado = rolService.buscarRoles(99L);

        // Assert
        assertFalse(resultado.isPresent());
        // Verifica que el método findById del repositorio fue llamado exactamente una vez con 99L
        verify(rolRepo, times(1)).findById(99L);
    }

    @Test
    void testActualizarRolExistente() {
        // Arrange
        Rol rolExistente = new Rol("USUARIO");
        rolExistente.setId(1L);

        Rol rolActualizadoData = new Rol("CLIENTE"); // Datos para actualizar

        // Configura el mock para que findById devuelva el rol existente
        when(rolRepo.findById(1L)).thenReturn(Optional.of(rolExistente));
        // Configura el mock para que save devuelva el objeto rolExistente modificado
        when(rolRepo.save(any(Rol.class))).thenReturn(rolExistente); // save devolverá la misma instancia modificada

        // Act
        Rol resultado = rolService.actualizarRol(rolActualizadoData, 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("CLIENTE", resultado.getNombre()); // Verifica que el nombre fue actualizado
        // Verifica que findById fue llamado una vez con 1L
        verify(rolRepo, times(1)).findById(1L);
        // Verifica que save fue llamado una vez con la instancia de rolExistente (que ahora tiene el nombre actualizado)
        verify(rolRepo, times(1)).save(rolExistente);
    }

    @Test
    void testActualizarRolNoExistenteDebeLanzarExcepcion() {
        // Arrange
        Rol rolParaActualizar = new Rol("ROL_INEXISTENTE");

        // Configura el mock para que findById devuelva un Optional vacío
        when(rolRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        // El método .get() en un Optional.empty() lanza NoSuchElementException en tu servicio
        assertThrows(java.util.NoSuchElementException.class, () -> {
            rolService.actualizarRol(rolParaActualizar, 99L);
        });

        // Verifica que findById fue llamado una vez
        verify(rolRepo, times(1)).findById(99L);
        // Asegura que save nunca fue llamado si el findById falla
        verify(rolRepo, never()).save(any(Rol.class));
    }

    @Test
    void testEliminarRolIdExistente() {
        // Arrange
        // Configura el mock para que deleteById no haga nada (simulando un borrado exitoso)
        doNothing().when(rolRepo).deleteById(1L);

        // Act
        Boolean resultado = rolService.eliminarRolId(1L);

        // Assert
        assertTrue(resultado);
        // Verifica que el método deleteById del repositorio fue llamado exactamente una vez
        verify(rolRepo, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarRolIdNoExistente() {
        // Arrange
        // Simula que deleteById lanza una excepción, lo que tu catch en el servicio maneja devolviendo false
        doThrow(new org.springframework.dao.EmptyResultDataAccessException(1)).when(rolRepo).deleteById(99L);

        // Act
        Boolean resultado = rolService.eliminarRolId(99L);

        // Assert
        assertFalse(resultado);
        // Verifica que el método deleteById del repositorio fue llamado exactamente una vez
        verify(rolRepo, times(1)).deleteById(99L);
    }
}