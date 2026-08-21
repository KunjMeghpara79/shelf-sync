package com.example.shelfsync;
import com.example.shelfsync.Models.Entities.Book;
import com.example.shelfsync.Repositories.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BooksInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;

    public BooksInitializer(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Book> bookList = List.of(
                new Book("The Midnight Library", 10, 10),
                new Book("Shadow of the Wind", 5, 5),
                new Book("Echoes of Eternity", 12, 12),
                new Book("Whispers in the Dark", 7, 7),
                new Book("The Alchemist's Secret", 15, 15),
                new Book("Chronicles of Eldoria", 8, 8),
                new Book("Beneath Crimson Skies", 4, 4),
                new Book("The Silent Patient", 20, 20),
                new Book("Starlight Odyssey", 9, 9),
                new Book("Secrets of the Forgotten", 6, 6)
        );
        bookRepository.saveAll(bookList);


    }
}
