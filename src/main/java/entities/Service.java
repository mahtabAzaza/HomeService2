package entities;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "services")
public class Service {


    // variables -------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    private String serviceName;
    private long serviceBasePrice;
    private String serviceDescription;
    private String serviceCategory;



    // relationships --------------------
    @OneToMany (mappedBy= "customer")
    private List<Order> orders;
    @ManyToMany (mappedBy= "specialist")
    private List<Proposal> Proposals;
    @ManyToMany
    private List<Specialist> specialists;

    @ManyToOne
    private Service parentService;

    @OneToMany(mappedBy = "parentService")
    private List<Service> childServices;


    // getter setter
    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public long getServiceBasePrice() {
        return serviceBasePrice;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public Service getParentService() {
        return parentService;
    }

    public List<Service> getChildServices() {
        return childServices;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceBasePrice(long serviceBasePrice) {
        this.serviceBasePrice = serviceBasePrice;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public void setProposals(List<Proposal> proposals) {
        Proposals = proposals;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setParentService(Service parentService) {
        this.parentService = parentService;
    }

    public void setChildServices(List<Service> childServices) {
        this.childServices = childServices;
    }
}
