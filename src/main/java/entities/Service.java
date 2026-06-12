package entities;


import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long serviceId;
    String serviceName;
    String serviceDescription;
    String serviceType;
    long serviceBasePrice;


    long orderID;
    long specialistID;
    long proposalID;

    @ManyToOne
    public Review review;




}
