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

import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.service.RolService;

class RolServiceTest {

    @InjectMocks
    private RolService rolService;

    @Mock
    private RolRepository rolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardar() {
        Rol rol = new Rol("Estudiante");

        when(rolRepository.save(any(Rol.class))).thenReturn(rol);

        Rol result = rolService.guardar(rol);

        assertNotNull(result);
        assertEquals("Estudiante", result.getNombre());
    }

    @Test
    void testVerRoles() {
        ArrayList<Rol> roles = new ArrayList<>();
        roles.add(new Rol("Estudiante"));

        when(rolRepository.findAll()).thenReturn(roles);

        ArrayList<Rol> result = rolService.verRoles();

        assertEquals(1, result.size());
        assertEquals("Estudiante", result.get(0).getNombre());
    }

    @Test
    void testBuscarRoles() {
        Rol rol = new Rol("Estudiante");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        Optional<Rol> result = rolService.buscarRoles(1L);

        assertTrue(result.isPresent());
        assertEquals("Estudiante", result.get().getNombre());
    }

    @Test
    void testActualizarRol() {
        Rol rol = new Rol("Estudiante");
        Rol updatedRol = new Rol("Profesor");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(Rol.class))).thenReturn(updatedRol);

        Rol result = rolService.actualizarRol(updatedRol, 1L);

        assertNotNull(result);
        assertEquals("Profesor", result.getNombre());
    }

    @Test
    void testEliminarRolId() {
        doNothing().when(rolRepository).deleteById(1L);

        Boolean result = rolService.eliminarRolId(1L);

        assertTrue(result);
    }
}