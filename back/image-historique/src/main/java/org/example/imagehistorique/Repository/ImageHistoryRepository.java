package org.example.imagehistorique.Repository;

import org.example.imagehistorique.Entity.ImageHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageHistoryRepository extends JpaRepository<ImageHistory, Long> {
}