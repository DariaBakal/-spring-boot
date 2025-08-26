package com.example.bookstore.repository.book;

import com.example.bookstore.model.Book;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Page<Book> findAllByCategoriesId(Long categoryId, Pageable pageable);

    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    Optional<Book> findById(Long id);
}
