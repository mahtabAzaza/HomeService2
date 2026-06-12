package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "customers")

public class Customer {

    //variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String email;
    private String password;
    private String customerRegisterDate;

    // relationships
    @OneToOne
    private Wallet wallet;
    @OneToMany
    private List<Order> orders;
    @ManyToMany
    private List<Review> reviews;


    //getter setter
    public long getId() {
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

    public String getCustomerRegisterDate() {
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
}
