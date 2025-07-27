package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public Book save(CreateBookRequestDto requestDto) {
        return bookRepository.save(requestDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public BookDto findById(Long id) {
        return null;
    }
}
