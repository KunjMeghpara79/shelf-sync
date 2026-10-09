package shelfsync.borrowchain;

import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Member;

public record Request(Book book, BookData bookData, Member member) {
}
