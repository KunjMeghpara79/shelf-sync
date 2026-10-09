package shelfsync.borrowchain;

import shelfsync.enums.MemberStatus;
import shelfsync.exceptions.RestrictedAccessException;

public class RestrictedMemberHandler implements BorrowHandler{
    private BorrowHandler next;
    @Override
    public void setNext(BorrowHandler next) {
        this.next = next;
    }

    @Override
    public void check(Request request) {
        if(request.member().getMemberStatus() == MemberStatus.RESTRICTED) throw new RestrictedAccessException("Member is restricted !");
        else next.check(request);
    }
}
