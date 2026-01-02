package com.example.image_service.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/images")
public class ImageController {

    private static final String UPLOAD_DIR = "uploads/";
    private final WebClient aiWebClient;
    private final WebClient gatewayWebClient;

    public ImageController() {
        // Client pour l'IA
        this.aiWebClient = WebClient.builder()
                .baseUrl("http://localhost:8005") 
                .build();

        // Client pour le Gateway
        this.gatewayWebClient = WebClient.builder()
                .baseUrl("http://localhost:8083") 
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. Sauvegarde locale
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        String imageUrl = "http://localhost:8082/images/" + fileName;

        // 2. Appel AI-Service
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath.toFile()));

        Map<String, Object> aiResult = aiWebClient.post()
                .uri("/predict")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block(); 

        // 3. Logique Historique (Appel au Gateway)
        try {
            if (aiResult != null) {
                Map<String, Object> details = (Map<String, Object>) aiResult.get("details");
                List<String> treatmentList = (List<String>) details.get("treatment");
                String predictedClass = (String) aiResult.get("predicted_class");

                Map<String, Object> historyRecord = new HashMap<>();
                historyRecord.put("fileName", fileName);
                historyRecord.put("imageUrl", imageUrl);
                historyRecord.put("plant", predictedClass.split("__")[0]); 
                historyRecord.put("disease", details.get("name"));
                historyRecord.put("treatment", String.join(" ; ", treatmentList));

                // Appel au Gateway sur le port 8089 vers /history
                gatewayWebClient.post()
                    .uri("/history") 
                    .bodyValue(historyRecord)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .doOnSuccess(s -> System.out.println("SUCCÈS : Enregistré dans XAMPP via Gateway"))
                    .doOnError(e -> System.err.println("ERREUR GATEWAY : " + e.getMessage()))
                    .block(); 
            }
        } catch (Exception e) {
            System.err.println("Erreur historique : " + e.getMessage());
        }

        // 4. Réponse
        Map<String, Object> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("prediction", aiResult);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(UPLOAD_DIR + fileName);
        Resource resource = new UrlResource(path.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}