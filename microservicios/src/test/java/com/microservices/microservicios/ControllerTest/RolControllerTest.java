package com.microservices.microservicios.ControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.microservicios.controller.RolController;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.service.RolService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testGetRoles() throws Exception {
        Rol rol1 = new Rol();
        rol1.setId(1L);
        rol1.setNombre("ADMIN");

        Rol rol2 = new Rol();
        rol2.setId(2L);
        rol2.setNombre("USER");

        ArrayList<Rol> roles = new ArrayList<>(Arrays.asList(rol1, rol2));

        when(rolService.verRoles()).thenReturn(roles);

        mockMvc.perform(get("/rol/roles") 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rolList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.rolList[0].nombre", is("ADMIN")))
                .andExpect(jsonPath("$._embedded.rolList[1].nombre", is("USER")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/rol/roles")));

        verify(rolService, times(1)).verRoles();
    }

    @Test
    void testCrearRol() throws Exception {
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre("EDITOR");

        Rol rolGuardado = new Rol();
        rolGuardado.setId(3L);
        rolGuardado.setNombre("EDITOR");

        when(rolService.guardar(any(Rol.class))).thenReturn(rolGuardado);

        mockMvc.perform(post("/rol/roles") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevoRol)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("EDITOR")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/rol/3"))); 

        verify(rolService, times(1)).guardar(any(Rol.class));
    }

    @Test
    void testBuscarRolExistente() throws Exception {
        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMIN");

        when(rolService.buscarRoles(1L)).thenReturn(Optional.of(rol));

        mockMvc.perform(get("/rol/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("ADMIN")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/rol/1")));

        verify(rolService, times(1)).buscarRoles(1L);
    }

    @Test
    void testBuscarRolNoExistente() throws Exception {
        when(rolService.buscarRoles(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/rol/{id}", 99L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).buscarRoles(99L);
    }

    @Test
    void testActualizarRolExistente() throws Exception {
        Rol rolActualizado = new Rol();
        rolActualizado.setId(1L);
        rolActualizado.setNombre("SUPER_ADMIN");

        when(rolService.actualizarRol(any(Rol.class), eq(1L))).thenReturn(rolActualizado);

        mockMvc.perform(put("/rol/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("SUPER_ADMIN")))
                .andExpect(jsonPath("$._links.self.href", is("http://localhost/rol/1")));

        verify(rolService, times(1)).actualizarRol(any(Rol.class), eq(1L));
    }

    @Test
    void testActualizarRolNoExistente() throws Exception {
        Rol rolParaActualizar = new Rol();
        rolParaActualizar.setNombre("ROL_INEXISTENTE");

        // El controlador espera 'null' para un 404
        when(rolService.actualizarRol(any(Rol.class), eq(99L))).thenReturn(null);

        mockMvc.perform(put("/rol/{id}", 99L) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolParaActualizar)))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).actualizarRol(any(Rol.class), eq(99L));
    }

    @Test
    void testEliminarRolExistente() throws Exception {
        when(rolService.eliminarRolId(1L)).thenReturn(true);

        mockMvc.perform(delete("/rol/{id}", 1L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("El Rol con la id 1 ha sido eliminado"));

        verify(rolService, times(1)).eliminarRolId(1L);
    }

    @Test
    void testEliminarRolNoExistente() throws Exception {
        when(rolService.eliminarRolId(99L)).thenReturn(false);

        mockMvc.perform(delete("/rol/{id}", 99L) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El Rol con la id 99 no existe"));

        verify(rolService, times(1)).eliminarRolId(99L);
    }
}