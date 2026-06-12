package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long orderID;
    long customerID;
    long specialistID;

    long score;
    String statement;

}
