package shelfsync.borrowchain;

import shelfsync.exceptions.BookAlreadyBorrowedException;

public class DuplicateCopyHandler implements BorrowHandler{
    private BorrowHandler next;
    @Override
    public void setNext(BorrowHandler next) {
        this.next = next;
    }

    @Override
    public void check(Request request) {
        if(request.member().getLoans().stream()
                .anyMatch(l -> l.getBook().getBookName().equals(request.bookData().getBookName()))){
            throw new BookAlreadyBorrowedException("This member has already borrowed one copy of this book");
        }
        else next.check(request);
    }
}
