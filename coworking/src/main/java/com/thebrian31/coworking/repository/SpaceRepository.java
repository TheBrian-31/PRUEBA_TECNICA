package com.thebrian31.coworking.repository;

import com.thebrian31.coworking.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {
}
