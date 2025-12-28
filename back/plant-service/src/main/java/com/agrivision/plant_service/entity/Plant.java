package com.agrivision.plant_service.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
@Entity
@Data
public class Plant {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String description;
    
}
