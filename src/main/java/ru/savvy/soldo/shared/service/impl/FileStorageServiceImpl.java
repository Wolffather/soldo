package ru.savvy.soldo.shared.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.savvy.soldo.shared.exception.IllegalOperationException;
import ru.savvy.soldo.shared.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path uploadPath;

    public FileStorageServiceImpl(@Value("${app.upload-dir:./uploads}") String uploadDir) {
        // Store images in the same subdirectory that FileServeController (document package) serves from
        this.uploadPath = Paths.get(uploadDir, "documents").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать директорию для файлов: " + uploadDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalOperationException("Файл пустой");
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;

        try {
            Path target = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Файл сохранён: {}", filename);
        } catch (IOException e) {
            throw new IllegalOperationException("Не удалось сохранить файл: " + e.getMessage());
        }

        return "/api/files/" + filename;
    }

    @Override
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) return;
        // strip leading /api/files/ if present
        String name = filename.replaceFirst("^/api/files/", "");
        try {
            Path target = uploadPath.resolve(name);
            Files.deleteIfExists(target);
            log.info("Файл удалён: {}", name);
        } catch (IOException e) {
            log.warn("Не удалось удалить файл {}: {}", name, e.getMessage());
        }
    }
}
