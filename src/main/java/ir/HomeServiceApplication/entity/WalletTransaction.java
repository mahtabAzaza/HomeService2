package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction extends BaseEntity<Long> {

    private Long amount;

    // CREDIT یا DEBIT
    private String type;

    private String description;

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
