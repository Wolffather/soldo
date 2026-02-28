package ru.savvy.soldo.service;

import ru.savvy.soldo.dto.DocumentTemplateDTO;

import java.util.List;

public interface DocumentTemplateService {

    List<DocumentTemplateDTO> getAll(String categoryFormat);

    DocumentTemplateDTO getById(Long id);

    DocumentTemplateDTO create(DocumentTemplateDTO dto);

    DocumentTemplateDTO update(Long id, DocumentTemplateDTO dto);

    void delete(Long id);
}
