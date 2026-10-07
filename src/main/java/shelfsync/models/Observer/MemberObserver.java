package shelfsync.models.Observer;

import shelfsync.models.entities.Member;

public interface MemberObserver {
    void memberRestricted(Member member);
}