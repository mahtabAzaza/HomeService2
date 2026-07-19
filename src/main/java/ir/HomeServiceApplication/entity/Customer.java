package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customers")
public class Customer extends User {


    @Column(name = "profile_picture", columnDefinition = "bytea")
    private byte[] profilePicture;

    // هر مشتری یک کیف پول دارد
    @OneToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    // هر مشتری می‌تواند چند سفارش ثبت کند
    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    // هر مشتری می‌تواند چند نظر ثبت کند
    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;
}
