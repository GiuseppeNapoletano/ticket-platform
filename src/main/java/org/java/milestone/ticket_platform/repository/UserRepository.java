package org.java.milestone.ticket_platform.repository;


import org.java.milestone.ticket_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Integer> {
    
}
