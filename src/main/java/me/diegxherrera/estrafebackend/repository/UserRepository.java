package me.diegxherrera.estrafebackend.repository;

import me.diegxherrera.estrafebackend.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username); // ✅ Use 'username' instead of 'email'
}