package entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "specialists")
public class Specialist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private String profileImage;


    @Enumerated(EnumType.STRING)
    private SpecialistStatus status;
    private LocalDateTime registerDate;

    @OneToOne
    private Wallet wallet;

    @ManyToMany
    @JoinTable(
            name = "services",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<Service> services;
    public Specialist() {
    }

}