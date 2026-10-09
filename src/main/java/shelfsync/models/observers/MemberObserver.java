package shelfsync.models.observers;

import shelfsync.models.entities.Member;

public interface MemberObserver {
    void memberRestricted(Member member);
}