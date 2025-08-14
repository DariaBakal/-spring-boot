package com.example.bookstore.repository.book.spec;

import com.example.bookstore.dto.book.PriceRange;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final String PRICE_KEY = "price";

    @Override
    public String getKey() {
        return PRICE_KEY;
    }

    @Override
    public Specification<Book> getSpecification(Object params) {
        PriceRange range = (PriceRange) params;
        BigDecimal priceFrom = range.priceFrom();
        BigDecimal priceTo = range.priceTo();
        return (root, query, criteriaBuilder) -> {
            if (priceFrom != null && priceTo != null) {
                return criteriaBuilder.between(root.get(PRICE_KEY), priceFrom, priceTo);
            } else if (priceFrom != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(PRICE_KEY), priceFrom);
            } else if (priceTo != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(PRICE_KEY), priceTo);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}

