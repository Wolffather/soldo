package ru.savvy.soldo.document.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.document.dto.DocumentTemplateDTO;
import ru.savvy.soldo.shared.exception.EntityFinder;
import ru.savvy.soldo.document.model.DocumentTemplate;
import ru.savvy.soldo.document.repository.DocumentTemplateRepository;
import ru.savvy.soldo.document.service.DocumentTemplateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentTemplateServiceImpl implements DocumentTemplateService {

    private final DocumentTemplateRepository repository;

    @Override
    public List<DocumentTemplateDTO> getAll(String categoryFormat) {
        List<DocumentTemplate> templates = categoryFormat != null && !categoryFormat.isBlank()
                ? repository.findByCategoryFormat(categoryFormat)
                : repository.findAll();
        return templates.stream().map(this::toDTO).toList();
    }

    @Override
    public DocumentTemplateDTO getById(Long id) {
        return toDTO(EntityFinder.findOrThrow(repository.findById(id), "Шаблон документа не найден: " + id));
    }

    @Override
    public DocumentTemplateDTO create(DocumentTemplateDTO dto) {
        DocumentTemplate template = DocumentTemplate.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .fileUrl(dto.getFileUrl())
                .categoryFormat(dto.getCategoryFormat())
                .isRequired(dto.getIsRequired() != null ? dto.getIsRequired() : true)
                .build();
        return toDTO(repository.save(template));
    }

    @Override
    public DocumentTemplateDTO update(Long id, DocumentTemplateDTO dto) {
        DocumentTemplate template = EntityFinder.findOrThrow(repository.findById(id), "Шаблон документа не найден: " + id);
        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setFileUrl(dto.getFileUrl());
        template.setCategoryFormat(dto.getCategoryFormat());
        if (dto.getIsRequired() != null) {
            template.setIsRequired(dto.getIsRequired());
        }
        return toDTO(repository.save(template));
    }

    @Override
    public void delete(Long id) {
        EntityFinder.findOrThrow(repository.findById(id), "Шаблон документа не найден: " + id);
        repository.deleteById(id);
    }

    private DocumentTemplateDTO toDTO(DocumentTemplate t) {
        return DocumentTemplateDTO.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .fileUrl(t.getFileUrl())
                .categoryFormat(t.getCategoryFormat())
                .isRequired(t.getIsRequired())
                .createdAt(t.getCreatedAt() != null ? t.getCreatedAt().toString() : null)
                .build();
    }
}
