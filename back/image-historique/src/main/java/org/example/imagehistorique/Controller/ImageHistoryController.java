package org.example.imagehistorique.Controller;

import java.util.List;

import org.example.imagehistorique.Entity.ImageHistory;
import org.example.imagehistorique.service.ImageHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/history")
@CrossOrigin("*")
public class ImageHistoryController {

    private final ImageHistoryService service;

    public ImageHistoryController(ImageHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ImageHistory> save(@RequestBody ImageHistory history) {
        return ResponseEntity.ok(service.save(history));
    }

    @GetMapping
    public ResponseEntity<List<ImageHistory>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
