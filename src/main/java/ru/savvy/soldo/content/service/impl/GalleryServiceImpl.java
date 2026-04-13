package ru.savvy.soldo.content.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.content.dto.GalleryItemDTO;
import ru.savvy.soldo.content.model.GalleryItem;
import ru.savvy.soldo.content.repository.GalleryItemRepository;
import ru.savvy.soldo.content.service.GalleryService;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.shared.service.FileStorageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final GalleryItemRepository repository;
    private final FileStorageService fileStorageService;

    @Override
    public List<GalleryItemDTO> getAll() {
        return repository.findAllByOrderBySortOrderAscCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public GalleryItemDTO create(String imageUrl, String caption) {
        GalleryItem item = GalleryItem.builder()
                .imageUrl(imageUrl)
                .caption(caption)
                .sortOrder(0)
                .build();
        return toDto(repository.save(item));
    }

    @Override
    public void delete(Long id) {
        GalleryItem item = EntityFinder.findOrThrow(repository.findById(id), "Фото не найдено: " + id);
        if (item.getImageUrl() != null && item.getImageUrl().startsWith("/api/files/")) {
            fileStorageService.delete(item.getImageUrl());
        }
        repository.deleteById(id);
    }

    private GalleryItemDTO toDto(GalleryItem e) {
        return GalleryItemDTO.builder()
                .id(e.getId())
                .imageUrl(e.getImageUrl())
                .caption(e.getCaption())
                .sortOrder(e.getSortOrder())
                .createdAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null)
                .build();
    }
}
