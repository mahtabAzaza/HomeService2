package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
public class Proposal {

    // variables --------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalID;
    private Long proposalPrice;
    private LocalDateTime startDate;
    private Integer duration;
    private LocalDateTime proposalRegistrationDate;

    //relationships --------------
    // هر پیشنهاد توسط یک متخصص ثبت می‌شود
    @ManyToOne
    @JoinColumn(name = "specialist_id")
    private Specialist specialist;
    // هر پیشنهاد برای یک سفارش ثبت می‌شود
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // getter setter

    public Long getProposalID() {
        return proposalID;
    }

    public Long getProposalPrice() {
        return proposalPrice;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Integer getDuration() {
        return duration;
    }

    public LocalDateTime getProposalRegistrationDate() {
        return proposalRegistrationDate;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public Order getOrder() {
        return order;
    }

    public void setProposalPrice(Long proposalPrice) {
        this.proposalPrice = proposalPrice;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setProposalRegistrationDate(LocalDateTime proposalRegistrationDate) {
        this.proposalRegistrationDate = proposalRegistrationDate;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
