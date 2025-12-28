package com.agrivision.plant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agrivision.plant_service.entity.DiseaseImage;

public interface DiseaseImageRepository extends JpaRepository<DiseaseImage, Long> {}
