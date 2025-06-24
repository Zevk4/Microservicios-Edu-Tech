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
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.repository.UsuarioRepository;
import com.microservices.microservicios.service.UsuarioService;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsuarios() {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario("John", "john@example.com", "password", new Rol("Estudiante")));

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        ArrayList<Usuario> result = usuarioService.getUsuarios();

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNombre());
    }

    @Test
    void testCrearUsuario() {
        Usuario usuario = new Usuario("John", "john@example.com", "password", null);
        Rol rol = new Rol("Estudiante");

        when(rolRepository.findByNombre("Estudiante")).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.crearUsuario(usuario);

        assertNotNull(result);
        assertEquals("John", result.getNombre());
    }

    @Test
    void testGetById() {
        Usuario usuario = new Usuario("John", "john@example.com", "password", new Rol("Estudiante"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("John", result.get().getNombre());
    }

    @Test
    void testUpdateById() {
        Usuario usuario = new Usuario("John", "john@example.com", "password", new Rol("Estudiante"));
        Usuario updatedUsuario = new Usuario("Jane", "jane@example.com", "newpassword", new Rol("Estudiante"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        Usuario result = usuarioService.updateById(updatedUsuario, 1L);

        assertNotNull(result);
        assertEquals("Jane", result.getNombre());
    }

    @Test
    void testDeleteById() {
        doNothing().when(usuarioRepository).deleteById(1L);

        Boolean result = usuarioService.deleteById(1L);

        assertTrue(result);
    }

    @Test
    void testChangeRol() {
        Usuario usuario = new Usuario("John", "john@example.com", "password", new Rol("Estudiante"));
        Rol newRol = new Rol("Profesor");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByNombre("Profesor")).thenReturn(Optional.of(newRol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = usuarioService.changeRol(1L, "Profesor");

        assertNotNull(result);
        assertEquals("Profesor", result.getRol().getNombre());
    }
}