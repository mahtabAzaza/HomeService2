package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "specialists")
public class Specialist extends User {

    @Lob
    @Column(name = "profile_image")
    private byte[] profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SpecialistStatus status;

    // هر متخصص یک کیف پول دارد
    @OneToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    // هر متخصص می‌تواند چند پیشنهاد ثبت کند
    @OneToMany(mappedBy = "specialist")
    private List<Proposal> proposals;

    // هر متخصص می‌تواند چند زیرخدمت انجام دهد
    @ManyToMany
    @JoinTable(
            name = "specialist_services",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;

    // هر متخصص می‌تواند در نهایت چند سفارش را انجام دهد
    @OneToMany(mappedBy = "specialist")
    private List<Order> orders;
}
