package service;

import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import entity.Customer;
import entity.Order;
import entity.Proposal;
import entity.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerService {

    CustomerResponseDto signup(CustomerSignupDto dto);

    Customer login(String email, String password);

    Customer findByEmail(String email);

    void updateProfile(Long customerId, CustomerSignupDto dto);

    List<Service> getServices();

    void placeOrder(Long customerId, Long serviceId, Long priceOffer,
                    LocalDateTime startDateTime, String address, String description);

    List<Order> getMyOrders(Long customerId);

    List<Proposal> getProposalsForOrder(Long orderId);

    void selectProposal(Long orderId, Long proposalId);

    void payOrder(Long orderId);

    void submitReview(Long customerId, Long orderId, int score, String statement);

    Long getWalletBalance(Long customerId);

    void chargeWallet(Long customerId, Long amount);
}
