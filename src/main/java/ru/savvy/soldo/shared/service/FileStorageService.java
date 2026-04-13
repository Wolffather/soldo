package ru.savvy.soldo.shared.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /** Сохраняет файл и возвращает публичный URL вида /api/files/{filename} */
    String store(MultipartFile file);

    /** Удаляет файл по имени (только имя файла, без пути и без /api/files/ префикса) */
    void delete(String filename);
}
