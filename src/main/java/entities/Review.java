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
    private long orderID;
    private int score;
    private String statement;

    // relationships ------------------
    @OneToOne
    private Order order;
    @OneToMany (mappedBy= "customer")
    private List<Customer> customers;

    // getter setter ------------------
    public long getOrderID() {
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

    public List<Customer> getCustomers() {
        return customers;
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

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }
}
