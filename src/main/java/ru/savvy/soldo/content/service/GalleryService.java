package ru.savvy.soldo.content.service;

import ru.savvy.soldo.content.dto.GalleryItemDTO;

import java.util.List;

public interface GalleryService {
    List<GalleryItemDTO> getAll();
    GalleryItemDTO create(String imageUrl, String caption);
    void delete(Long id);
}
