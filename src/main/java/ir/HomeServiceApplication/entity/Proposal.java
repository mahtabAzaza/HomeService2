package ir.HomeServiceApplication.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "proposals")
public class Proposal extends BaseEntity<Long> {

    private Long proposalPrice;

    private LocalDateTime startDate;

    // مدت زمان انجام کار (به ساعت)
    private Integer duration;

    // هر پیشنهاد توسط یک متخصص ثبت می‌شود
    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    // هر پیشنهاد برای یک سفارش ثبت می‌شود
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
