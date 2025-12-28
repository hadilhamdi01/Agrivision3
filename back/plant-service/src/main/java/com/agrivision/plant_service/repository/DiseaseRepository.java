package com.agrivision.plant_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrivision.plant_service.entity.Disease;
import com.agrivision.plant_service.entity.Plant;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
            Optional<Disease> findByName(String name);


}