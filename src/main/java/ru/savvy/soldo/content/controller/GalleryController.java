package ru.savvy.soldo.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.savvy.soldo.content.dto.GalleryItemDTO;
import ru.savvy.soldo.content.service.GalleryService;
import ru.savvy.soldo.shared.service.FileStorageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;
    private final FileStorageService fileStorageService;

    /** Public: список фотографий галереи */
    @GetMapping("/public/gallery")
    public ResponseEntity<List<GalleryItemDTO>> getAll() {
        return ResponseEntity.ok(galleryService.getAll());
    }

    /** Admin: загрузить фото в галерею */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping(value = "/gallery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GalleryItemDTO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption) {
        String url = fileStorageService.store(file);
        GalleryItemDTO dto = galleryService.create(url, caption);
        return ResponseEntity.ok(dto);
    }

    /** Admin: удалить фото из галереи */
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @DeleteMapping("/gallery/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        galleryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
