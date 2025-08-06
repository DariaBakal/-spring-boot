package com.example.bookstore.repository.book;

import com.example.bookstore.dto.BookSearchParameters;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.SpecificationBuilder;
import com.example.bookstore.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String AUTHOR_KEY = "author";
    private static final String PRICE_KEY = "price";
    private static final String TITLE_KEY = "title";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = (
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(
                    bookSpecificationProviderManager.getSpecificationProvider(AUTHOR_KEY)
                            .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.range() != null) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(PRICE_KEY)
                    .getSpecification(searchParameters.range()));
        }
        if (searchParameters.titlePart() != null) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(TITLE_KEY)
                    .getSpecification(searchParameters.titlePart()));
        }
        return spec;
    }
}
