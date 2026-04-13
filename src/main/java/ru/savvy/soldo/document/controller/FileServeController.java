package ru.savvy.soldo.document.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.document.service.FileStorageService;

@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileServeController {

    private final FileStorageService fileStorageService;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource resource = fileStorageService.loadAsResource(filename);

            String contentType = "application/octet-stream";
            String name = filename.toLowerCase();
            if (name.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (name.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (name.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (name.endsWith(".xls")) {
                contentType = "application/vnd.ms-excel";
            } else if (name.endsWith(".xlsx")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (name.endsWith(".png")) {
                contentType = "image/png";
            } else if (name.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (name.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (name.endsWith(".svg")) {
                contentType = "image/svg+xml";
            }

            // Изображения и PDF открываются в браузере (inline), остальные — как скачивание
            boolean isInline = name.endsWith(".pdf")
                    || name.endsWith(".jpg") || name.endsWith(".jpeg")
                    || name.endsWith(".png") || name.endsWith(".gif")
                    || name.endsWith(".webp") || name.endsWith(".svg");
            String disposition = isInline ? "inline" : "attachment";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            disposition + "; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (SecurityException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.warn("Файл не найден: {}", filename);
            return ResponseEntity.notFound().build();
        }
    }
}
