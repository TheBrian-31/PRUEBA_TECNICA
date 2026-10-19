package com.thebrian31.coworking.repository;

import com.thebrian31.coworking.entity.Role;
import com.thebrian31.coworking.entity.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {

    Optional<Role> findByCode(RoleCode code);
}
