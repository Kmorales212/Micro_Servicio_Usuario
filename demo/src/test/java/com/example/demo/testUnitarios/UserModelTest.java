package com.example.demo.testUnitarios;

import com.example.demo.model.UserModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Long id = 1L;
        String name = "Juan";
        String email = "juan@test.com";
        String pass = "123";
        String role = "ADMIN";

        UserModel user = new UserModel(id, name, email, pass, role);

        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(pass, user.getPassword());
        assertEquals(role, user.getRole());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UserModel user = new UserModel();
        user.setId(2L);
        user.setName("Maria");
        user.setEmail("maria@test.com");
        user.setPassword("abc");
        user.setRole("USER");

        assertEquals(2L, user.getId());
        assertEquals("Maria", user.getName());
        assertEquals("maria@test.com", user.getEmail());
        assertEquals("abc", user.getPassword());
        assertEquals("USER", user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        UserModel user1 = new UserModel(1L, "Juan", "j@t.com", "123", "ADMIN");
        UserModel user2 = new UserModel(1L, "Juan", "j@t.com", "123", "ADMIN");
        UserModel user3 = new UserModel(2L, "Pedro", "p@t.com", "456", "USER");

        assertEquals(user1, user2);      
        assertNotEquals(user1, user3);  
        
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        UserModel user = new UserModel(1L, "Juan", "j@t.com", "123", "ADMIN");
        String toString = user.toString();

        assertTrue(toString.contains("Juan"));
        assertTrue(toString.contains("j@t.com"));
        assertTrue(toString.contains("id=1"));
    }
}