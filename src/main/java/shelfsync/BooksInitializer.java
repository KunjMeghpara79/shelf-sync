package shelfsync;
import shelfsync.Models.Entities.BookData;
import shelfsync.Repositories.BookRepository;
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
        List<BookData> bookDataList = List.of(
                new BookData("The Midnight Library", 10, 10),
                new BookData("Shadow of the Wind", 5, 5),
                new BookData("Echoes of Eternity", 12, 12),
                new BookData("Whispers in the Dark", 7, 7),
                new BookData("The Alchemist's Secret", 15, 15),
                new BookData("Chronicles of Eldoria", 8, 8),
                new BookData("Beneath Crimson Skies", 4, 4),
                new BookData("The Silent Patient", 20, 20),
                new BookData("Starlight Odyssey", 9, 9),
                new BookData("Secrets of the Forgotten", 6, 6)
        );
        bookRepository.saveAll(bookDataList);


    }
}
