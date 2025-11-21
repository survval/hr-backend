package com.smarthireflow.hrbackend.user.repository;

import com.smarthireflow.hrbackend.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}