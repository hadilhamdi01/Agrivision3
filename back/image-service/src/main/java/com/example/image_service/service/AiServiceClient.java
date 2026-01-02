package com.example.image_service.service;

// Import des classes nécessaires pour gérer les requêtes HTTP, multipart, et les services Spring
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

// Annotation @Service → indique que cette classe est un service Spring injectable
@Service
public class AiServiceClient {

    // RestTemplate permet de faire des appels HTTP vers d'autres services (ici vers l'AI-Service)
    private final RestTemplate restTemplate = new RestTemplate();

    // URL de l'AI-Service via Gateway
    // Attention : ici elle est codée en dur, on peut la remplacer par Eureka plus tard pour la découverte dynamique
    private final String aiServiceUrl = "http://localhost:8005/api/ai"; 

    /**
     * Méthode pour envoyer une image au service AI et récupérer la prédiction
     * @param file l'image envoyée par l'utilisateur
     * @return le résultat de la prédiction sous forme de JSON (String)
     * @throws Exception si l'envoi ou la conversion de fichier échoue
     */
    public String predict(MultipartFile file) throws Exception {

        // Création du corps de la requête HTTP multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // On ajoute le fichier à la requête, en le convertissant en ressource compatible HTTP
        // MultipartInputStreamFileResource permet de transformer le MultipartFile en ByteArrayResource avec nom du fichier
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        // Création des headers HTTP
        HttpHeaders headers = new HttpHeaders();
        // Type de contenu multipart/form-data (obligatoire pour l'envoi de fichiers)
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Création de l'entité HTTP complète : corps + headers
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Appel HTTP POST vers l'endpoint /predict de l'AI-Service via Gateway
        // postForEntity renvoie un objet ResponseEntity contenant le status, headers et le body
        ResponseEntity<String> response = restTemplate.postForEntity(aiServiceUrl + "/predict", requestEntity, String.class);

        // Retourne le body de la réponse, ici le JSON contenant la classe prédite et le taux de confiance
        return response.getBody();
    }
}
