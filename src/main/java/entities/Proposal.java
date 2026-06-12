package entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "proposals")
public class Proposal {


    // variables --------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int proposalID;
    private long proposalPrice;
    private String startDate;
    private String duration;
    private String proposalRegistrationDate;

    //relationships --------------
    @ManyToOne
    private Specialist specialist;
    @ManyToOne
    private Order order;


    // getter setter
    public int getProposalID() {
        return proposalID;
    }

    public long getProposalPrice() {
        return proposalPrice;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getDuration() {
        return duration;
    }

    public String getProposalRegistrationDate() {
        return proposalRegistrationDate;
    }

    public Specialist getSpecialist() {
        return specialist;
    }

    public Order getOrder() {
        return order;
    }

    public void setProposalPrice(long proposalPrice) {
        this.proposalPrice = proposalPrice;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setProposalRegistrationDate(String proposalRegistrationDate) {
        this.proposalRegistrationDate = proposalRegistrationDate;
    }

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
