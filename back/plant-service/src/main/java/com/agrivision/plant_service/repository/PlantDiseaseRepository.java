package com.agrivision.plant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrivision.plant_service.entity.PlantDisease;

public interface PlantDiseaseRepository extends JpaRepository<PlantDisease, Long> {}

