package entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reviews")
public class Review {

    // variables ---------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderID;
    private int score;
    private String statement;

    // relationships ------------------
    @OneToOne
    private Order order;
    @ManyToOne
    private Customer customer;
    @OneToMany (mappedBy= "customer")
    private List<Review> reviews;

    // getter setter ------------------
    public Long getOrderID() {
        return orderID;
    }

    public int getScore() {
        return score;
    }

    public String getStatement() {
        return statement;
    }

    public Order getOrder() {
        return order;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setOrderID(long orderID) {
        this.orderID = orderID;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
