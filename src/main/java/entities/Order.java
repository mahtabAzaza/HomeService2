package entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    // variable --------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String orderDescription;
    private long priceOffer;
    private LocalDateTime orderStartDateTime;
    private String address;
    private LocalDateTime orderRegisterDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // relationships -------
    // هر سفارش متعلق به یک مشتری است
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    // هر سفارش مربوط به یک خدمت یا زیرخدمت است
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;
    // هر سفارش می‌تواند چند پیشنهاد داشته باشد
    @OneToMany(mappedBy = "order")
    private List<Proposal> proposals;
    // متخصص انتخاب شده برای انجام این سفارش
    @ManyToOne
    @JoinColumn(name = "selected_specialist_id")
    private Specialist specialist;


    // getter setter
    public Long getOrderId() {
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

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
