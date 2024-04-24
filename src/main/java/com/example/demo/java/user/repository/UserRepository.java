package com.example.demo.java.user.repository;

import com.example.demo.java.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<String, User> {

    Optional<User> findById(String id);

}
