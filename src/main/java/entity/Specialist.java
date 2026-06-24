package entity;

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
    private byte[] profileImage;
    private LocalDateTime registerDate;
    @Enumerated(EnumType.STRING)
    private SpecialistStatus status;

    //relationships --------------------------
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

    //constructor
    public Specialist (){
    }


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

    public byte[] getProfileImage() {
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

    public List<Proposal> getProposals() {
        return proposals;
    }

    public List<Service> getServices() {
        return services;
    }

    public List<Order> getOrders() {
        return orders;
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

    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public void setStatus(SpecialistStatus status) {
        this.status = status;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setProposals(List<Proposal> proposals) {
        this.proposals = proposals;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}