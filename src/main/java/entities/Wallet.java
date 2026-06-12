package entities;


import jakarta.persistence.*;


@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int walletID;
    int specialistID;
    int customerID;
    long balance;
    String customerName;
    String specialistName;



}
