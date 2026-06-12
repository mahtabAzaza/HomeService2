package entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    // variable -------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long orderId;
    private String orderDescription;
    private long priceOffer;
    private LocalDateTime orderStartDateTime;
    private String address;
    private LocalDateTime orderRegisterDate;
    private OrderStatus orderStatus;


    // relationships --------------
    @ManyToOne
    private Customer customer;
    @OneToMany
    private List<Proposal> proposals;
    @ManyToOne
    private Service service;
    @OneToOne
    private Specialist specialist;


    // getter setter
    public long getOrderId() {
        return orderId;
    }

    public String getOrderDescription() {
        return orderDescription;
    }

    public long getPriceOffer() {
        return priceOffer;
    }

    public LocalDateTime getOrderStartDateTime() {
        return orderStartDateTime;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getOrderRegisterDate() {
        return orderRegisterDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Proposal> getProposals() {
        return proposals;
    }

    public Service getService() {
        return service;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }

    public void setPriceOffer(long priceOffer) {
        this.priceOffer = priceOffer;
    }

    public void setOrderStartDateTime(LocalDateTime orderStartDateTime) {
        this.orderStartDateTime = orderStartDateTime;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProposals(List<Proposal> proposals) {
        this.proposals = proposals;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }
}
