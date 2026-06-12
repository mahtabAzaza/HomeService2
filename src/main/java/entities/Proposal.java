package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "proposals")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int proposalID;
    long proposalPrice;
    String startDate;
    String duration;
    String proposalRegistrationDate;

    long specialistID;
    long customerID;
    long orderID;


}
