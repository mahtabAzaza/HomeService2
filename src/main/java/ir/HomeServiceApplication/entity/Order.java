package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity<Long> {

    private String orderDescription;

    private Long priceOffer;

    private LocalDateTime orderStartDateTime;

    private String address;

    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

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

    // پیشنهاد انتخاب شده — برای محاسبه زمان پیشنهادی پایان کار و تشخیص تاخیر
    @ManyToOne
    @JoinColumn(name = "selected_proposal_id")
    private Proposal selectedProposal;

    // زمان واقعی پایان کار — ثبت می‌شود وقتی مشتری markOrderDone را صدا می‌زند
    private LocalDateTime completionTime;
}
