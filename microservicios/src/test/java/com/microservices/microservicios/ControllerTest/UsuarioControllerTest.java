package com.microservices.microservicios.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.microservicios.controller.UsuarioController;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString; 
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testVerUsuarios() throws Exception {
        Rol rolMock = new Rol();
        rolMock.setId(1L);
        rolMock.setNombre("ADMIN");

        Usuario user1 = new Usuario();
        user1.setId(1L);
        user1.setNombre("Juan Perez");
        user1.setEmail("juan.perez@example.com");
        user1.setPassword("pass123");
        user1.setRol(rolMock);

        Usuario user2 = new Usuario();
        user2.setId(2L);
        user2.setNombre("Maria Lopez");
        user2.setEmail("maria.lopez@example.com");
        user2.setPassword("pass456");
        user2.setRol(rolMock);

        ArrayList<Usuario> usuarios = new ArrayList<>(Arrays.asList(user1, user2));

        when(usuarioService.getUsuarios()).thenReturn(usuarios);

        mockMvc.perform(get("/usuario/usuarios") 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.usuarioList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.usuarioList[0].nombre", is("Juan Perez")))
                .andExpect(jsonPath("$._embedded.usuarioList[0].email", is("juan.perez@example.com")))
                .andExpect(jsonPath("$._embedded.usuarioList[0].rol.nombre", is("ADMIN")))
                .andExpect(jsonPath("$._embedded.usuarioList[1].nombre", is("Maria Lopez")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/usuario/usuarios"))); 

        verify(usuarioService, times(1)).getUsuarios();
    }

    @Test
    void testCrearUsuario() throws Exception {
        Rol rolMock = new Rol();
        rolMock.setId(2L);
        rolMock.setNombre("USER");

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Pedro Gomez");
        nuevoUsuario.setEmail("pedro.gomez@example.com");
        nuevoUsuario.setPassword("securepass");
        nuevoUsuario.setRol(rolMock);

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setId(3L);
        usuarioGuardado.setNombre("Pedro Gomez");
        usuarioGuardado.setEmail("pedro.gomez@example.com");
        usuarioGuardado.setPassword("securepass");
        usuarioGuardado.setRol(rolMock);

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuarioGuardado);

        mockMvc.perform(post("/usuario/crearUsuario") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoUsuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Pedro Gomez")))
                .andExpect(jsonPath("$.email", is("pedro.gomez@example.com")))
                .andExpect(jsonPath("$.rol.nombre", is("USER")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/usuario/3")));

        verify(usuarioService, times(1)).crearUsuario(any(Usuario.class));
    }

    @Test
    void testBuscarUsuarioExistente() throws Exception {
        Rol rolMock = new Rol();
        rolMock.setId(1L);
        rolMock.setNombre("ADMIN");

        Usuario user = new Usuario();
        user.setId(1L);
        user.setNombre("Juan Perez");
        user.setEmail("juan.perez@example.com");
        user.setPassword("pass123");
        user.setRol(rolMock);

        when(usuarioService.getById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/usuario/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Perez")))
                .andExpect(jsonPath("$.email", is("juan.perez@example.com")))
                .andExpect(jsonPath("$.rol.nombre", is("ADMIN")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/usuario/1")));

        verify(usuarioService, times(1)).getById(1L);
    }

    @Test
    void testBuscarUsuarioNoExistente() throws Exception {
        when(usuarioService.getById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuario/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).getById(99L);
    }

    @Test
    void testActualizarUsuarioExistente() throws Exception {
        Rol rolMock = new Rol();
        rolMock.setId(1L);
        rolMock.setNombre("ADMIN");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setNombre("Juan Perez Actualizado");
        usuarioActualizado.setEmail("juan.perez.updated@example.com");
        usuarioActualizado.setPassword("newpass");
        usuarioActualizado.setRol(rolMock);

        when(usuarioService.updateById(any(Usuario.class), eq(1L))).thenReturn(usuarioActualizado);

        mockMvc.perform(put("/usuario/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Perez Actualizado")))
                .andExpect(jsonPath("$.email", is("juan.perez.updated@example.com")))
                .andExpect(jsonPath("$.rol.nombre", is("ADMIN")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/usuario/1")));

        verify(usuarioService, times(1)).updateById(any(Usuario.class), eq(1L));
    }

    @Test
    void testActualizarUsuarioNoExistente() throws Exception {
        Usuario usuarioParaActualizar = new Usuario();
        usuarioParaActualizar.setNombre("Usuario Inexistente");
        usuarioParaActualizar.setEmail("no.existe@example.com");

        when(usuarioService.updateById(any(Usuario.class), eq(99L))).thenReturn(null);

        mockMvc.perform(put("/usuario/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioParaActualizar)))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).updateById(any(Usuario.class), eq(99L));
    }

    @Test
    void testEliminarUsuarioExistente() throws Exception {
        when(usuarioService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/usuario/eliminar{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("El usuario con la id 1 ha sido eliminado")); 

        verify(usuarioService, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarUsuarioNoExistente() throws Exception {
        when(usuarioService.deleteById(99L)).thenReturn(false);

        mockMvc.perform(delete("/usuario/eliminar{id}", 99L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El usuario con la id 99 no existe")); 

        verify(usuarioService, times(1)).deleteById(99L);
    }

    @Test
    void testChangeRolUsuarioExistente() throws Exception {
        Rol oldRolMock = new Rol();
        oldRolMock.setId(2L);
        oldRolMock.setNombre("USER");

        Rol newRolMock = new Rol();
        newRolMock.setId(1L);
        newRolMock.setNombre("ADMIN");

        Usuario existingUser = new Usuario();
        existingUser.setId(1L);
        existingUser.setNombre("Juan Perez");
        existingUser.setEmail("juan.perez@example.com");
        existingUser.setPassword("pass123");
        existingUser.setRol(oldRolMock);

        Usuario updatedUser = new Usuario();
        updatedUser.setId(1L);
        updatedUser.setNombre("Juan Perez");
        updatedUser.setEmail("juan.perez@example.com");
        updatedUser.setPassword("pass123");
        updatedUser.setRol(newRolMock);

        when(usuarioService.changeRol(eq(1L), eq("ADMIN"))).thenReturn(updatedUser);

        mockMvc.perform(put("/usuario/cambiarRol/{id}", 1L) 
                .param("newRoleName", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Juan Perez")))
                .andExpect(jsonPath("$.rol.nombre", is("ADMIN")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/usuario/1")));

        verify(usuarioService, times(1)).changeRol(eq(1L), eq("ADMIN"));
    }

    @Test
    void testChangeRolUsuarioNoExistente() throws Exception {
        when(usuarioService.changeRol(eq(99L), anyString())).thenReturn(null); //Retorna null

        mockMvc.perform(put("/usuario/cambiarRol/{id}", 99L)
                .param("newRoleName", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Espera 404, ya que el controlador devuelve 404 para null del servicio

        verify(usuarioService, times(1)).changeRol(eq(99L), anyString());
    }

    @Test
    void testChangeRolRolNoExistente() throws Exception {
        Rol oldRolMock = new Rol();
        oldRolMock.setId(2L);
        oldRolMock.setNombre("USER");

        Usuario existingUser = new Usuario();
        existingUser.setId(1L);
        existingUser.setNombre("Juan Perez");
        existingUser.setEmail("juan.perez@example.com");
        existingUser.setPassword("pass123");
        existingUser.setRol(oldRolMock);

        // Mock del servicio para que retorne null si el rol no se encuentra (para que el controlador devuelva 404)
        when(usuarioService.changeRol(eq(1L), eq("NON_EXISTENT_ROLE"))).thenReturn(null); // Retorna null

        mockMvc.perform(put("/usuario/cambiarRol/{id}", 1L)
                .param("newRoleName", "NON_EXISTENT_ROLE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); 

        verify(usuarioService, times(1)).changeRol(eq(1L), eq("NON_EXISTENT_ROLE"));
    }
}