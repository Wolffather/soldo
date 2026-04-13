package ru.savvy.soldo.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.content.model.GalleryItem;

import java.util.List;

public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
    List<GalleryItem> findAllByOrderBySortOrderAscCreatedAtDesc();
}
