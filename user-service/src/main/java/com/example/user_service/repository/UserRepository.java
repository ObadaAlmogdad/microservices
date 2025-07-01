package com.example.user_service.repository;

import com.example.user_service.model.User;
import com.example.user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByActive(boolean active);
    List<User> findByRoleAndActive(Role role, boolean active);
}

