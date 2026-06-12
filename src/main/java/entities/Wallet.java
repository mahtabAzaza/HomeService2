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


    // relationships-------------------
    @OneToOne(cascade = CascadeType.ALL)
    private long specialistID;
    @OneToOne(cascade = CascadeType.ALL)
    private long customerID;





}
