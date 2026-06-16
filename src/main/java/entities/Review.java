package entities;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "reviews")
public class Review {

    // variables ---------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewID;
    private int score;
    private String statement;

    // relationships ------------------
    // مشتری که نظر را ثبت کرده
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    // متخصصی که درباره او نظر داده شده
    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;
    @OneToOne
    private Order order;

    // getter setter ------------------

    public Long getOrderID() {
        return reviewID;
    }

    public int getScore() {
        return score;
    }

    public String getStatement() {
        return statement;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public Long getReviewID() {
        return reviewID;
    }

    public void setReviewID(Long reviewID) {
        this.reviewID = reviewID;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
