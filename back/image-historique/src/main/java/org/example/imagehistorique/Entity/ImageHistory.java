package org.example.imagehistorique.Entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "image_history")
@Data
public class ImageHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Empêche l'insertion si le nom du fichier est absent
    private String fileName;

    @Column(nullable = false)
    private String imageUrl;

    private String plant;
    private String disease;
    
    @Column(columnDefinition = "TEXT")
    private String treatment;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Cette méthode s'exécute automatiquement juste avant l'insertion dans MySQL
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}