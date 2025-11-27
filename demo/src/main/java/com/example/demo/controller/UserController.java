package com.example.demo.controller;

import com.example.demo.model.UserModel;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- 1. REGISTRO (POST /usuarios) ---
    // Este método faltaba en lo que me enviaste. Es vital para crear usuarios.
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserModel user) {
        try {
            // El servicio se encarga de encriptar la contraseña antes de guardar
            UserModel savedUser = userService.saveUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar: " + e.getMessage());
        }
    }

    // --- 2. LOGIN (POST /usuarios/login) ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel loginRequest) {
        try {
            // Llamamos al servicio. Si la contraseña está mal, lanza excepción.
            String token = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            
            // Devolvemos el token en un formato JSON limpio: { "token": "..." }
            Map<String, String> response = Collections.singletonMap("token", token);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Si falla (usuario no existe o pass incorrecta), devuelve 401 Unauthorized
            // Devolvemos el error también en JSON para que el Front lo lea mejor
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // --- 3. MÉTODOS CRUD (GET, PUT, DELETE) ---

    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserModel> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable Long id, @RequestBody UserModel userDetails) {
        UserModel updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}