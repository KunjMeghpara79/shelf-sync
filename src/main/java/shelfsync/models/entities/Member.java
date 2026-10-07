package shelfsync.models.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import shelfsync.enums.MemberStatus;
import shelfsync.models.Observer.Interfaces.MemberObserver;
import shelfsync.services.interfaces.EmailService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(name = "member_name")
    private String memberName;

    @Transient
    private List<MemberObserver> observers = new ArrayList<>();
    @Email
    @Column(name = "member_email", unique = true,nullable = false)
    private String memberEmail;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Loan> loans = new HashSet<>();

    @Column(name = "fine")
    private int fine = 0;

    @Enumerated(EnumType.STRING)
    private MemberStatus memberStatus = MemberStatus.ACTIVE;

    public void restrictMember() {

        this.setMemberStatus(MemberStatus.RESTRICTED);
        for(MemberObserver observer : this.getObservers()){
            observer.memberRestricted(this);
        }

    }

}
