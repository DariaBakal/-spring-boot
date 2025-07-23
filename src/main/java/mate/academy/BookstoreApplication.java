package mate.academy;

import java.math.BigDecimal;
import mate.academy.model.Book;
import mate.academy.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BookstoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book testBook = new Book();
            testBook.setTitle("The Clockmaker's Secret");
            testBook.setAuthor("Eliza Thornton");
            testBook.setIsbn("978-1-23456-789-7");
            testBook.setPrice(new BigDecimal("18.99"));
            testBook.setDescription("A gripping historical mystery set in Victorian London,"
                    + " uncovering a hidden family legacy.");
            testBook.setCoverImage("https://example.com/images/clockmaker.jpg");
            bookService.save(testBook);
            System.out.println(bookService.findAll());
        };
    }
}
