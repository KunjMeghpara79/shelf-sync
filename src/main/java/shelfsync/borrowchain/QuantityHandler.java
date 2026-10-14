package shelfsync.borrowchain;

import shelfsync.exceptions.NotFoundException;

public class QuantityHandler implements BorrowHandler{
    @Override
    public void setNext(BorrowHandler next) {
    }

    @Override
    public void check(Request request) {
        if(request.bookData().getTotalQuantity() - request.bookData().getLoans().size() <= 0) throw new NotFoundException("No copies Available!");
    }
}
