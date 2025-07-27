package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import java.util.List;

public interface BookService {
    Book save(CreateBookRequestDto requestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);
}
