package shelfsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import shelfsync.models.entities.Admin;
import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.repositories.AdminRepository;
import shelfsync.repositories.BookDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import shelfsync.repositories.BookRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class BooksInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final BookDataRepository bookDataRepository;
    public BooksInitializer(AdminRepository adminRepository, PasswordEncoder passwordEncoder, BookRepository bookRepository, BookDataRepository bookDataRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.bookRepository = bookRepository;
        this.bookDataRepository = bookDataRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setAdminEmail("admin@gmail.com");
            admin.setAdminName("Admin");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            adminRepository.save(admin);

        }
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
            int totalQty = 3 + (i % 3);
            int availableQty = totalQty;

            BookData bookData = new BookData(title, totalQty, availableQty);
            bookData.setBooks(new HashSet<>());
            bookData = bookDataRepository.save(bookData);

            for (int j = 1; j <= totalQty; j++) {
                Book book = new Book();
                book.setBookName(title + " - Copy " + j);
                book.setBookData(bookData);
                book.setLoan(null);
                bookRepository.save(book);
            }
        }

    }
}
