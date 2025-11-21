package com.smarthireflow.hrbackend.user.service;

import com.smarthireflow.hrbackend.user.entity.UserEntity;
import com.smarthireflow.hrbackend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public Optional<UserEntity> findOne(Long userId) {
      return repo.findById(userId);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return repo.existsByEmailAndIdNot(email, id);
    }

    public long count() {
        return repo.count();
    }

    public UserEntity save(UserEntity user) {
        return repo.save(user);
    }

    public List<UserEntity> findAll() {
        return repo.findAll();
    }

    public UserEntity findOneOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }
}