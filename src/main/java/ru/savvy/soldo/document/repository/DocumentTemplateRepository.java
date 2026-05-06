package ru.savvy.soldo.document.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.document.model.DocumentTemplate;

import java.util.List;

public interface DocumentTemplateRepository extends JpaRepository<DocumentTemplate, Long> {

    List<DocumentTemplate> findByCategoryFormat(String categoryFormat);

    List<DocumentTemplate> findByEventId(Long eventId);
}
