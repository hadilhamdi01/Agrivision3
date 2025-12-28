package com.agrivision.plant_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class PlantDisease {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Plant plant;

    @ManyToOne
    private Disease disease;
}

