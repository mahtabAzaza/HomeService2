package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.entity.*;

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

    List<Proposal> getProposalsForOrder(Long orderId, ProposalSortByType sortBy);

    void selectProposal(Long orderId, Long proposalId);
    void markOrderStarted(Long orderId);

    void markOrderDone(Long orderId);


    void payOrder(Long orderId);

    void submitReview(Long customerId, Long orderId, int score, String statement);

    Long getWalletBalance(Long customerId);

    void chargeWallet(Long customerId, Long amount);
}
