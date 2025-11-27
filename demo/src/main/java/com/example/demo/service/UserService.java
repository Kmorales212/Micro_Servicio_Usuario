package com.example.demo.service;

import com.example.demo.model.UserModel;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil; // Asegúrate de que este paquete sea correcto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Para encriptar
    private final JwtUtil jwtUtil; // Para generar el token

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // --- REGISTRO CON ENCRIPTACIÓN ---
    public UserModel saveUser(UserModel user) {
        // Encriptamos la contraseña antes de guardar en la BD
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // --- LOGIN CON JWT ---
    public String login(String email, String passwordRaw) {
        // 1. Buscar usuario por email
        // NOTA: Asegúrate de tener 'findByEmail' en tu UserRepository
        UserModel user = userRepository.findByEmail(email); 
        
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // 2. Verificar contraseña (la que ingresa el usuario vs la encriptada)
        if (!passwordEncoder.matches(passwordRaw, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Si todo es correcto, generar y devolver el Token
        return jwtUtil.generateToken(user.getEmail());
    }

    // --- MÉTODOS CRUD EXISTENTES ---

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public UserModel updateUser(Long id, UserModel userDetails) {
        if (userRepository.existsById(id)) {
            userDetails.setId(id);
            // OJO: Si aquí actualizas la contraseña, deberías encriptarla también.
            // Por seguridad, idealmente la contraseña no se actualiza en este método general,
            // sino en uno específico de "changePassword".
            return userRepository.save(userDetails);
        }
        return null; 
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}