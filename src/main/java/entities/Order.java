package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "proposals")
public class Order {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long orderId;
    OrderStatus  orderStatus;
    String serviceType;
    long serviceID;
    String orderDescription;

    String orderStartDate;
    String orderEndDate;
    long orderPrice;
    String orderAddress;

    long customerId;

    long specialistId;




}
