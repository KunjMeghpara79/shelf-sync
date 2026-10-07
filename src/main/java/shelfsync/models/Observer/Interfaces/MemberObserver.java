package shelfsync.models.Observer.Interfaces;

import shelfsync.models.entities.Member;

public interface MemberObserver {
    void memberRestricted(Member member);
}