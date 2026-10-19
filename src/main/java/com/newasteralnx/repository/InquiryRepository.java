package com.newasteralnx.repository;

import com.newasteralnx.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, JpaSpecificationExecutor<Inquiry> {
    boolean existsByInquiryNumber(String inquiryNumber);
}
