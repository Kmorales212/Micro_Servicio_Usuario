package com.example.demo.testUnitarios;

import com.example.demo.service.UserService;
import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private UserService userService;

    private UserModel user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);


        userService = new UserService(userRepository, passwordEncoder, jwtUtil);

        user = new UserModel(1L, "Juan Perez", "juan@test.com", "password123", "CLIENT");
    }

    @Test
    void testSaveUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        UserModel savedUser = userService.saveUser(new UserModel(null, "Juan Perez", "juan@test.com", "password123", "CLIENT"));

        assertNotNull(savedUser);
        assertEquals("juan@test.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail("juan@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("fake-jwt-token");

        Map<String, Object> response = userService.login("juan@test.com", "password123");

        assertEquals("fake-jwt-token", response.get("token"));
        assertEquals("CLIENT", response.get("role"));
        assertEquals(1L, response.get("id"));
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("error@test.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            userService.login("error@test.com", "pass");
        });
    }

    @Test
    void testLoginWrongPassword() {
        when(userRepository.findByEmail("juan@test.com")).thenReturn(user);
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            userService.login("juan@test.com", "wrong");
        });
    }
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserModel> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Juan Perez", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Preparar
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Actuar
        Optional<UserModel> result = userService.getUserById(1L);

        // Verificar
        assertTrue(result.isPresent());
        assertEquals("juan@test.com", result.get().getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser_Success() {
        UserModel userDetails = new UserModel(null, "Juan Actualizado", "juan@test.com", "newpass", "CLIENT");
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.save(any(UserModel.class))).thenReturn(userDetails);

        UserModel updatedUser = userService.updateUser(1L, userDetails);

        assertNotNull(updatedUser);
        assertEquals("Juan Actualizado", updatedUser.getName());
        verify(userRepository, times(1)).save(userDetails);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        UserModel result = userService.updateUser(99L, new UserModel());

        assertNull(result);
        verify(userRepository, never()).save(any(UserModel.class));
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}