package ru.savvy.soldo.inquiry.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.savvy.soldo.inquiry.dto.InquiryDto;

public interface InquiryService {

    void create(InquiryDto dto);

    Page<InquiryDto> getAll(Pageable pageable);

    void delete(Long id);
}
