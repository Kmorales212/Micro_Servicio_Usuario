package com.example.demo.testUnitarios;

import com.example.demo.controller.UserController;
import com.example.demo.model.UserModel;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString; // Necesario para verificar mensajes de error
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;
    private ObjectMapper objectMapper;
    private UserModel user;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        objectMapper = new ObjectMapper();
        
        user = new UserModel(1L, "Juan Perez", "juan@test.com", "password123", "CLIENT");

        UserController userController = new UserController(userService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }


    @Test
    void testCreateUser_Success() throws Exception {
        when(userService.saveUser(any(UserModel.class))).thenReturn(user);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void testCreateUser_DefaultRole() throws Exception {
        UserModel userSinRol = new UserModel(1L, "Juan", "juan@test.com", "123", "");
        
        when(userService.saveUser(any(UserModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userSinRol)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CLIENT")); 
    }

    @Test
    void testCreateUser_Exception() throws Exception {
        when(userService.saveUser(any(UserModel.class))).thenThrow(new RuntimeException("Error de base de datos"));

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Error al registrar")));
    }


    @Test
    void testLogin_Success() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "fake-jwt");
        response.put("role", "CLIENT");

        when(userService.login(anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt"));
    }

    @Test
    void testLogin_Failure() throws Exception {
        when(userService.login(anyString(), anyString())).thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }


    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Juan Perez")); 
    }

    @Test
    void testGetUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testUpdateUser_Success() throws Exception {
        when(userService.updateUser(eq(1L), any(UserModel.class))).thenReturn(user);

        mockMvc.perform(put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Perez"));
    }

    @Test
    void testUpdateUser_NotFound() throws Exception {
        when(userService.updateUser(anyLong(), any(UserModel.class))).thenReturn(null);

        mockMvc.perform(put("/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }


    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent()); 
        
        verify(userService, times(1)).deleteUser(1L);
    }
}