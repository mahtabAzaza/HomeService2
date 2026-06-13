package service;

import entities.Order;

public interface OrderService {

//    add order
    void addOrder(Order order);
//    show proposals
    void showProposals (Long orderId);
//    chose specialist
    void choseSpecialist(Long proposalId);
 // order done
    void  orderSubmission();
    void updateOrderStatus(Long orderId);
//    pay
    void payForOrder(Long orderId);
}
