package org.example.imagehistorique.service;

import java.time.LocalDateTime;
import java.util.List;

import org.example.imagehistorique.Entity.ImageHistory;
import org.example.imagehistorique.Repository.ImageHistoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ImageHistoryService {
    private final ImageHistoryRepository repository;

    public ImageHistoryService(ImageHistoryRepository repository) {
        this.repository = repository;
    }

    public ImageHistory save(ImageHistory history) {
        history.setCreatedAt(LocalDateTime.now());
        return repository.save(history);
    }

    public List<ImageHistory> findAll() {
        return repository.findAll();
    }
}