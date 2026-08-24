package shelfsync;
import shelfsync.Models.Entities.Book;
import shelfsync.Models.Entities.BookData;
import shelfsync.Repositories.BookDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import shelfsync.Repositories.BookRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class BooksInitializer implements CommandLineRunner {

    private final BookDataRepository bookDataRepository;
    private final BookRepository bookRepository;
    public BooksInitializer(BookDataRepository bookDataRepository, BookRepository bookRepository) {
        this.bookDataRepository = bookDataRepository;

        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args){
        if (bookDataRepository.count() > 0) {
            return;
        }

        List<String> dummyTitles = Arrays.asList(
                "The Great Gatsby",
                "To Kill a Mockingbird",
                "1984",
                "Pride and Prejudice",
                "The Hobbit"
        );

        for (int i = 0; i < dummyTitles.size(); i++) {
            String title = dummyTitles.get(i);
            int totalQty = 3 + (i % 3); // Generates copies between 3 and 5
            int availableQty = totalQty; // Initializing all as available

            // 1. Create and Save BookData parent record
            BookData bookData = new BookData(title, totalQty, availableQty);
            bookData.setBooks(new HashSet<>());
            bookData = bookDataRepository.save(bookData);

            // 2. Generate individual Book copies for this title
            for (int j = 1; j <= totalQty; j++) {
                Book book = new Book();
                // Ensure unique name/barcode constraint (e.g., "1984 - Copy 1")
                book.setBookName(title + " - Copy " + j);
                book.setBookData(bookData);
                book.setLoan(null); // Explicitly setting loan to null initially
                bookRepository.save(book);
            }
        }


    }
}
