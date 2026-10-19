package com.newasteralnx.repository;

import com.newasteralnx.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface QuoteRepository extends JpaRepository<Quote, Long>, JpaSpecificationExecutor<Quote> {
    boolean existsByQuoteNumber(String quoteNumber);
}
