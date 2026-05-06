package ru.savvy.soldo.document.service;

import ru.savvy.soldo.document.dto.DocumentTemplateDTO;

import java.util.List;

public interface DocumentTemplateService {

    List<DocumentTemplateDTO> getAll(String categoryFormat);

    List<DocumentTemplateDTO> getByEventId(Long eventId);

    DocumentTemplateDTO getById(Long id);

    DocumentTemplateDTO create(DocumentTemplateDTO dto);

    DocumentTemplateDTO update(Long id, DocumentTemplateDTO dto);

    void delete(Long id);
}
