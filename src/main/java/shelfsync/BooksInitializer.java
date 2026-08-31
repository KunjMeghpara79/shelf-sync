package shelfsync;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import shelfsync.models.entities.Admin;
import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.repositories.AdminRepository;
import shelfsync.repositories.BookDataRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import shelfsync.repositories.BookRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class BooksInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final BookDataRepository bookDataRepository;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

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
            admin.setAdminEmail(adminEmail);
            admin.setAdminName("Admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
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

        List<String> dummyAuthors = Arrays.asList(
                "F. Scott Fitzgerald",
                "Harper Lee",
                "George Orwell",
                "Jane Austen",
                "J.R.R. Tolkien"
        );

        List<BookData> bookDataListToSave = new ArrayList<>();
        List<Book> booksToSave = new ArrayList<>();

        for (int i = 0; i < dummyTitles.size(); i++) {
            String title = dummyTitles.get(i);
            String author = dummyAuthors.get(i); // Fetch matching author by index

            int totalQty = 3 + (i % 3);

            // 2. Initialize BookData with the author name
            BookData bookData = new BookData(title, totalQty, totalQty);
            bookData.setAuthorName(author);
            bookData.setBooks(new HashSet<>());
            bookDataListToSave.add(bookData);

            // 3. Generate child Book items with the author name
            for (int j = 1; j <= totalQty; j++) {
                Book book = new Book();
                book.setBookName(title);
                book.setBookData(bookData);
                book.setLoan(null);

                bookData.getBooks().add(book);
                booksToSave.add(book);
            }
        }

        bookDataRepository.saveAll(bookDataListToSave);
        bookRepository.saveAll(booksToSave);


    }
}
