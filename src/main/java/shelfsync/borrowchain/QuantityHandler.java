package shelfsync.borrowchain;

import shelfsync.exceptions.BookNotAvailableException;

public class QuantityHandler implements BorrowHandler{
    @Override
    public void setNext(BorrowHandler next) {
    }

    @Override
    public void check(Request request) {
        if(request.bookData().getTotalQuantity() - request.bookData().getLoans().size() <= 0) throw new BookNotAvailableException("No copies Available!");
    }
}
