package ru.savvy.soldo.inquiry.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.inquiry.dto.InquiryDto;
import ru.savvy.soldo.inquiry.model.Inquiry;
import ru.savvy.soldo.inquiry.repository.InquiryRepository;
import ru.savvy.soldo.inquiry.service.InquiryService;

@Service
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;

    @Override
    @Transactional
    public void create(InquiryDto dto) {
        Inquiry inquiry = Inquiry.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .eventTitle(dto.getEventTitle())
                .message(dto.getMessage())
                .build();
        inquiryRepository.save(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryDto> getAll(Pageable pageable) {
        return inquiryRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        inquiryRepository.deleteById(id);
    }

    private InquiryDto toDto(Inquiry inquiry) {
        InquiryDto dto = new InquiryDto();
        dto.setId(inquiry.getId());
        dto.setName(inquiry.getName());
        dto.setPhone(inquiry.getPhone());
        dto.setEmail(inquiry.getEmail());
        dto.setEventTitle(inquiry.getEventTitle());
        dto.setMessage(inquiry.getMessage());
        dto.setCreatedAt(inquiry.getCreatedAt());
        return dto;
    }
}
