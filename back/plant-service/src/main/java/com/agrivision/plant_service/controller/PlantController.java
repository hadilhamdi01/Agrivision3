package com.agrivision.plant_service.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import com.agrivision.plant_service.entity.Plant;
import com.agrivision.plant_service.repository.PlantRepository;

@RestController
@RequestMapping("/plants")
@CrossOrigin
public class PlantController {

    @Autowired
    private PlantRepository plantRepository;

    @GetMapping
    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }

    @GetMapping("/{id}")
    public Plant getPlantById(@PathVariable Long id) {
        return plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
    }

    @PostMapping
    public Plant createPlant(@RequestBody Plant plant) {
        return plantRepository.save(plant);
    }

    @PutMapping("/{id}")
    public Plant updatePlant(@PathVariable Long id, @RequestBody Plant updatedPlant) {
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        plant.setName(updatedPlant.getName());
        plant.setDescription(updatedPlant.getDescription());

        return plantRepository.save(plant);
    }

    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable Long id) {
        plantRepository.deleteById(id);
    }
}
