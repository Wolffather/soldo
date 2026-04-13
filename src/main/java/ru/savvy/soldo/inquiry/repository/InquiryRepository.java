package ru.savvy.soldo.inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.inquiry.model.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
