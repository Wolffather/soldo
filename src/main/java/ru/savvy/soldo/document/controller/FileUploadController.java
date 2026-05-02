package ru.savvy.soldo.document.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.savvy.soldo.document.service.FileStorageService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/admin/upload")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class FileUploadController {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private static final long MAX_SIZE_BYTES = 20 * 1024 * 1024; // 20 MB

    private final FileStorageService fileStorageService;

    @PostMapping
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Файл пустой"));
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Файл слишком большой. Максимум 20 МБ"));
        }

        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Недопустимый тип файла. Разрешены: PDF, DOC, DOCX, XLS, XLSX"));
        }

        try {
            String storedName = fileStorageService.store(file);
            String url = "/files/" + storedName;
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            log.error("Ошибка при загрузке файла", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Ошибка при сохранении файла"));
        }
    }
}
