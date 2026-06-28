package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reviews")
public class Review extends BaseEntity<Long> {

    @Min(1)
    @Max(5)
    private Integer score;

    // متن نظر (اختیاری)
    private String statement;

    // مشتری که نظر را ثبت کرده
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // متخصصی که درباره او نظر داده شده
    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;

    // سفارشی که برای آن نظر داده شده
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
