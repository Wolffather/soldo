package ru.savvy.soldo.document.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:./uploads}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath, "documents").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
            log.info("Upload directory: {}", this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать директорию для загрузки файлов: " + this.uploadDir, e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        );
        // Sanitize: replace spaces and special characters
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._\\-а-яА-ЯёЁ]", "_");
        String storedName = UUID.randomUUID() + "_" + safeName;

        Path target = uploadDir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        log.info("Сохранён файл: {}", storedName);
        return storedName;
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path file = uploadDir.resolve(filename).normalize();
        // Security: prevent path traversal
        if (!file.startsWith(uploadDir)) {
            throw new SecurityException("Попытка path traversal: " + filename);
        }
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new IllegalArgumentException("Файл не найден: " + filename);
    }

    public void delete(String filename) {
        try {
            Path file = uploadDir.resolve(filename).normalize();
            if (file.startsWith(uploadDir)) {
                Files.deleteIfExists(file);
                log.info("Удалён файл: {}", filename);
            }
        } catch (IOException e) {
            log.warn("Не удалось удалить файл {}: {}", filename, e.getMessage());
        }
    }
}
