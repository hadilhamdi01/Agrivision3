package com.agrivision.plant_service;

import com.agrivision.plant_service.entity.Disease;
import com.agrivision.plant_service.entity.DiseaseImage;
import com.agrivision.plant_service.entity.Plant;
import com.agrivision.plant_service.entity.PlantDisease;
import com.agrivision.plant_service.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlantRepository plantRepo;
    private final DiseaseRepository diseaseRepo;
    private final DiseaseImageRepository imageRepo;
    private final PlantDiseaseRepository plantDiseaseRepo;

    public DataLoader(PlantRepository plantRepo,
                      DiseaseRepository diseaseRepo,
                      DiseaseImageRepository imageRepo,
                      PlantDiseaseRepository plantDiseaseRepo) {
        this.plantRepo = plantRepo;
        this.diseaseRepo = diseaseRepo;
        this.imageRepo = imageRepo;
        this.plantDiseaseRepo = plantDiseaseRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        String datasetPath = "C:/Users/ADMIN/OneDrive - South Mediterranean University/Desktop/datasets/plant_disease/train";
       // String datasetPath = "C:/Users/ADMIN/OneDrive - South Mediterranean University/Desktop/datasets/plant_disease/train";

        File trainFolder = new File(datasetPath);

        if (!trainFolder.exists() || !trainFolder.isDirectory()) {
            System.out.println("Dataset non trouvé : " + datasetPath);
            System.out.println("Chargement ignoré.");
            return;
        }

        System.out.println("Chargement du dataset en cours...");

        int totalImages = 0;

        for (File folder : trainFolder.listFiles(File::isDirectory)) {
            String folderName = folder.getName(); // ex: "Tomato___Late_blight"
            String[] parts = folderName.split("___");
            if (parts.length < 2) continue;

            String plantName = parts[0].trim();
            String diseaseName = parts[1].trim();

            // 1. Créer ou récupérer la plante
            Plant plant = plantRepo.findByName(plantName)
                    .orElseGet(() -> {
                        Plant p = new Plant();
                        p.setName(plantName);
                        System.out.println("Nouvelle plante : " + plantName);
                        return plantRepo.save(p);
                    });

            // 2. Créer ou récupérer la maladie
            Disease disease = diseaseRepo.findByName(diseaseName)
                    .orElseGet(() -> {
                        Disease d = new Disease();
                        d.setName(diseaseName);
                        System.out.println("Nouvelle maladie : " + diseaseName);
                        return diseaseRepo.save(d);
                    });

            // 3. Créer le lien Plant <-> Disease (si pas déjà existant)
            boolean linkExists = plantDiseaseRepo.findAll().stream()
                    .anyMatch(pd -> pd.getPlant().getId().equals(plant.getId()) &&
                                    pd.getDisease().getId().equals(disease.getId()));

            if (!linkExists) {
                PlantDisease pd = new PlantDisease();
                pd.setPlant(plant);
                pd.setDisease(disease);
                plantDiseaseRepo.save(pd);
                System.out.println("Lien créé : " + plantName + " → " + diseaseName);
            }

            // 4. Ajouter toutes les images
            File[] images = folder.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                    name.toLowerCase().endsWith(".jpeg") ||
                    name.toLowerCase().endsWith(".png"));

            if (images != null) {
                for (File img : images) {
                    DiseaseImage di = new DiseaseImage();
                    di.setDisease(disease);
                    di.setImagePath(img.getAbsolutePath());
                    imageRepo.save(di);
                    totalImages++;
                }
            }
        }

        System.out.println("Dataset chargé avec succès !");
        System.out.println(totalImages + " images ajoutées en base.");
    }
}