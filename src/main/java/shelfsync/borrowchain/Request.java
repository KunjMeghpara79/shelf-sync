package shelfsync.borrowchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shelfsync.models.entities.Book;
import shelfsync.models.entities.BookData;
import shelfsync.models.entities.Member;

@Getter
@AllArgsConstructor
public class Request {
    private final Book book;
    private final BookData bookData;
    private final Member member;
}
