package com.agrivision.plant_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrivision.plant_service.entity.Plant;

public interface PlantRepository extends JpaRepository<Plant, Long> {
        Optional<Plant> findByName(String name);

}
