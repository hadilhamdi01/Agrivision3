package com.agrivision.plant_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
@Entity
@Data
public class Disease {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String symptoms;
    private String treatment;
    public void setPlant(Plant plant) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPlant'");
    }
}
