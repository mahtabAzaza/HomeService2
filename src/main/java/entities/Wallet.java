package entities;


import jakarta.persistence.*;


@Entity
@Table(name = "wallets")
public class Wallet {


    //variables-----------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int walletID;
    private String ownerName;
    private long balance;


     //relationships-------------------
    @OneToOne
    private long specialistID;
    @OneToOne
    private long customerID;

    //getter setter


    public int getWalletID() {
        return walletID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public long getSpecialistID() {
        return specialistID;
    }

    public long getCustomerID() {
        return customerID;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setSpecialistID(long specialistID) {
        this.specialistID = specialistID;
    }

    public void setCustomerID(long customerID) {
        this.customerID = customerID;
    }
}
