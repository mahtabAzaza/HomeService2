package entities;


import jakarta.persistence.*;


@Entity
@Table(name = "wallets")
public class Wallet {


    //variables-----------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletID;
    private String ownerName;
    private long balance;


     //relationships-------------------
    @OneToOne
    private  Specialist specialist;
    @OneToOne
    private Customer customer;

    //getter setter


    public Long getWalletID() {
        return walletID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }
}
