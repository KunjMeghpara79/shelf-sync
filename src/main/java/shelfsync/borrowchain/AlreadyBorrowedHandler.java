package shelfsync.borrowchain;

import shelfsync.exceptions.BookNotAvailableException;

public class AlreadyBorrowedHandler implements BorrowHandler{
    private BorrowHandler next;
    @Override
    public void setNext(BorrowHandler next) {
        this.next = next;
    }

    @Override
    public void check(Request request) {
        if(request.getBook().getLoan() != null) throw new BookNotAvailableException("This book is already borrowed !");
        else next.check(request);
    }
}
