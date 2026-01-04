package com.example.image_service.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/images")
public class ImageController {

    private static final String UPLOAD_DIR = "uploads/";

    private final WebClient aiWebClient;
    private final WebClient gatewayWebClient;

    public ImageController() {
        this.aiWebClient = WebClient.builder()
                .baseUrl("http://localhost:8005")
                .build();

        this.gatewayWebClient = WebClient.builder()
                .baseUrl("http://localhost:8083") // ✅ GATEWAY
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authorizationHeader // ✅ JWT
    ) throws IOException {

        // 1️⃣ Sauvegarde locale
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        String imageUrl = "http://localhost:8082/images/" + fileName;

        // 2️⃣ Appel AI-Service
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(filePath.toFile()));

        Map<String, Object> aiResult = aiWebClient.post()
                .uri("/predict")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        // 3️⃣ Enregistrement Historique via Gateway
        if (aiResult != null) {
            Map<String, Object> details = (Map<String, Object>) aiResult.get("details");

            Map<String, Object> historyRecord = new HashMap<>();
            historyRecord.put("fileName", fileName);
            historyRecord.put("imageUrl", imageUrl);
            historyRecord.put("plant", aiResult.get("predicted_class"));
            historyRecord.put("disease", details.get("name"));
            historyRecord.put("treatment", String.join(" ; ", (List<String>) details.get("treatment")));

            gatewayWebClient.post()
                .uri("/history")
                .header("Authorization", authorizationHeader) // ✅ JWT TRANSMIS
                .bodyValue(historyRecord)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> System.out.println("✅ Historique enregistré"))
                .doOnError(e -> System.err.println("❌ Erreur Gateway : " + e.getMessage()))
                .block();
        }

        // 4️⃣ Réponse
        Map<String, Object> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        response.put("prediction", aiResult);

        return ResponseEntity.ok(response);
    }
}
