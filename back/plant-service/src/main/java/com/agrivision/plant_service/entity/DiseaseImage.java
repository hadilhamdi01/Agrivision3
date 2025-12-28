package com.agrivision.plant_service.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
@Entity
@Data
public class DiseaseImage {
    @Id @GeneratedValue
    private Long id;
    private String imagePath;

    @ManyToOne
    private Disease disease;
}
