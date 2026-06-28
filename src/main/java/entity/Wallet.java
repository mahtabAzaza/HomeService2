package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity<Long> {

    private String ownerName;

    private Long balance;

    // کیف پول متخصص
    @OneToOne(mappedBy = "wallet")
    private Specialist specialist;

    // کیف پول مشتری
    @OneToOne(mappedBy = "wallet")
    private Customer customer;
}
