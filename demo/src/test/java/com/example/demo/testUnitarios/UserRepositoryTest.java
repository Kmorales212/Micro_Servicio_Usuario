package com.example.demo.testUnitarios;

import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRepositoryTest {

    private UserRepository userRepository; 
    private UserModel user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        
        user = new UserModel(1L, "Juan", "juan@test.com", "pass123", "USER");
    }

    @Test
    void testFindByEmail_Success() {
        when(userRepository.findByEmail("juan@test.com")).thenReturn(user);

        UserModel found = userRepository.findByEmail("juan@test.com");

        assertNotNull(found);
        assertEquals("Juan", found.getName());
        verify(userRepository, times(1)).findByEmail("juan@test.com");
    }

    @Test
    void testFindByEmail_NotFound() {
        when(userRepository.findByEmail("noexiste@test.com")).thenReturn(null);

        // Ejecutar
        UserModel found = userRepository.findByEmail("noexiste@test.com");

        assertNull(found);
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        UserModel saved = userRepository.save(user);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
    }
}