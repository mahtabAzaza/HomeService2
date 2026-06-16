package entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    //variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private String password;
    private LocalDateTime customerRegisterDate;

    // relationships
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

    // constructor
    public Customer() {
    }

    //getter setter
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

    public LocalDateTime getCustomerRegisterDate() {
        return customerRegisterDate;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public List<Review> getReviews() {
        return reviews;
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

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setCustomerRegisterDate(LocalDateTime customerRegisterDate) {
        this.customerRegisterDate = customerRegisterDate;
    }
}
