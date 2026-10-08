package shelfsync.borrowchain;

public interface BorrowHandler {
    void setNext(BorrowHandler next);

    void check(Request request);
}
