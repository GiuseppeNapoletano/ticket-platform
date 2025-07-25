package org.java.milestone.ticket_platform.repository;


import java.util.List;
import java.util.Optional;

import org.java.milestone.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Integer> {
    Optional<User> findByUsername(String username);
    List<User> findByRolesNameAndStatus(String roleName, User.UserStatus status);
}
