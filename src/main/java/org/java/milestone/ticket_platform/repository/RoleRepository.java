package org.java.milestone.ticket_platform.repository;

import java.util.List;

import org.java.milestone.ticket_platform.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository <Role, Integer>{
    List<Role> findByName(String name);
}
