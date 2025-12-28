package com.agrivision.plant_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.agrivision.plant_service.entity.Disease;
import com.agrivision.plant_service.entity.Plant;
import com.agrivision.plant_service.entity.PlantDisease;
import com.agrivision.plant_service.repository.DiseaseRepository;
import com.agrivision.plant_service.repository.PlantDiseaseRepository;
import com.agrivision.plant_service.repository.PlantRepository;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/plant-diseases")
@CrossOrigin
public class PlantDiseaseController {

    @Autowired
    private PlantDiseaseRepository plantDiseaseRepository;

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @GetMapping
    public List<PlantDisease> getAll() {
        return plantDiseaseRepository.findAll();
    }

    @PostMapping("/{plantId}/{diseaseId}")
    public PlantDisease linkPlantDisease(
            @PathVariable Long plantId,
            @PathVariable Long diseaseId
    ) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new RuntimeException("Disease not found"));

        PlantDisease pd = new PlantDisease();
        pd.setPlant(plant);
        pd.setDisease(disease);

        return plantDiseaseRepository.save(pd);
    }

    @DeleteMapping("/{id}")
    public void deleteLink(@PathVariable Long id) {
        plantDiseaseRepository.deleteById(id);
    }
}
