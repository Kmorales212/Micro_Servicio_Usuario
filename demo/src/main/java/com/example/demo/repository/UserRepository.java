package com.example.demo.repository;

import com.example.demo.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    // Spring crea automáticamente el SQL: "SELECT * FROM usuario WHERE email = ?"
    UserModel findByEmail(String email);

}