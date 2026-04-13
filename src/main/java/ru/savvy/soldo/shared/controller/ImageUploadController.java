package ru.savvy.soldo.shared.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.savvy.soldo.shared.service.FileStorageService;

import java.util.Map;

/**
 * Handles image uploads for site content (team photos, gallery, panel backgrounds).
 * File serving is handled by the existing FileServeController in the document package.
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class ImageUploadController {

    private final FileStorageService fileStorageService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        String url = fileStorageService.store(file);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
