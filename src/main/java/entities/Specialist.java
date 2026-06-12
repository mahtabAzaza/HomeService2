package entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "specialists")
public class Specialist {

    // variables ---------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private byte profileImage;
    private LocalDateTime registerDate;

    @Enumerated(EnumType.STRING)
    private SpecialistStatus status;

    //relationships --------------------------
    @OneToOne
    private Wallet wallet;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Proposal> proposals;
    @ManyToMany
    private List <Service> services;


//    @ManyToMany
//    @JoinTable(
//            name = "services",
//            joinColumns = @JoinColumn(name = "specialist_id"),
//            inverseJoinColumns = @JoinColumn(name = "service_id")
//    )


    // getter setter--------------------------------
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public byte getProfileImage() {
        return profileImage;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public SpecialistStatus getStatus() {
        return status;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProfileImage(byte profileImage) {
        this.profileImage = profileImage;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }


}