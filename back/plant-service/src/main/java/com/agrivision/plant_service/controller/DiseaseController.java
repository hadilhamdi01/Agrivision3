package com.agrivision.plant_service.controller;

import org.springframework.web.bind.annotation.*;

import com.agrivision.plant_service.entity.Disease;
import com.agrivision.plant_service.repository.DiseaseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/diseases")
@CrossOrigin
public class DiseaseController {

    @Autowired
    private DiseaseRepository diseaseRepository;

    @GetMapping
    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    @GetMapping("/{id}")
    public Disease getDiseaseById(@PathVariable Long id) {
        return diseaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disease not found"));
    }

    @PostMapping
    public Disease createDisease(@RequestBody Disease disease) {
        return diseaseRepository.save(disease);
    }

    @PutMapping("/{id}")
    public Disease updateDisease(@PathVariable Long id, @RequestBody Disease updatedDisease) {
        Disease disease = diseaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disease not found"));

        disease.setName(updatedDisease.getName());
        disease.setSymptoms(updatedDisease.getSymptoms());
        disease.setTreatment(updatedDisease.getTreatment());

        return diseaseRepository.save(disease);
    }

    @DeleteMapping("/{id}")
    public void deleteDisease(@PathVariable Long id) {
        diseaseRepository.deleteById(id);
    }
}

