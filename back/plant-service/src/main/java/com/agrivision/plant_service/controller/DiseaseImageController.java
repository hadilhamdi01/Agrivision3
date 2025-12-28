package com.agrivision.plant_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.agrivision.plant_service.entity.Disease;
import com.agrivision.plant_service.entity.DiseaseImage;
import com.agrivision.plant_service.repository.DiseaseImageRepository;
import com.agrivision.plant_service.repository.DiseaseRepository;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/disease-images")
@CrossOrigin
public class DiseaseImageController {

    @Autowired
    private DiseaseImageRepository imageRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @GetMapping
    public List<DiseaseImage> getAllImages() {
        return imageRepository.findAll();
    }
    @GetMapping("/disease/{diseaseId}")
    public List<DiseaseImage> getImagesByDisease(@PathVariable Long diseaseId) {
        return imageRepository.findAll().stream()
                .filter(img -> img.getDisease() != null && diseaseId.equals(img.getDisease().getId()))
                .collect(Collectors.toList());
    }
    

    @PostMapping("/{diseaseId}")
    public DiseaseImage addImage(@PathVariable Long diseaseId, @RequestBody DiseaseImage image) {
        Disease disease = diseaseRepository.findById(diseaseId)
                .orElseThrow(() -> new RuntimeException("Disease not found"));

        image.setDisease(disease);
        return imageRepository.save(image);
    }

    @DeleteMapping("/{id}")
    public void deleteImage(@PathVariable Long id) {
        imageRepository.deleteById(id);
    }
}

