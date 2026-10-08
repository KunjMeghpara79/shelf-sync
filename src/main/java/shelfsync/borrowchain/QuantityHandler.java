package shelfsync.borrowchain;

import shelfsync.exceptions.BookNotAvailableException;

public class QuantityHandler implements BorrowHandler{
    private BorrowHandler next;
    @Override
    public void setNext(BorrowHandler next) {
        this.next = next;
    }

    @Override
    public void check(Request request) {
        if(request.getBookData().getTotalQuantity() - request.getBookData().getLoans().size() <= 0) throw new BookNotAvailableException("No copies Available!");
    }
}
