package controller;

import DTO.CustomerSignupDto;
import DTO.OrderDto;
import DTO.ProposalDto;
import DTO.ServiceDto;
import entity.Customer;
import entity.Order;
import entity.Proposal;
import entity.Service;
import mapper.OrderMapper;
import mapper.ProposalMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.UserContext;
import service.CustomerService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        return ResponseEntity.ok(customerService.getServices());
    }

    @PostMapping("/orders")
    public ResponseEntity<Void> placeOrder(@RequestBody OrderDto dto) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.placeOrder(
                current.getId(),
                dto.getServiceId(),
                dto.getPriceOffer(),
                dto.getOrderStartDateTime(),
                dto.getAddress(),
                dto.getOrderDescription()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getMyOrders() {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        List<Order> orders = customerService.getMyOrders(current.getId());
        List<OrderDto> dtos = orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/orders/{orderId}/proposals")
    public ResponseEntity<List<ProposalDto>> getProposals(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "price") String sortBy) {
        List<Proposal> proposals = customerService.getProposalsForOrder(orderId, sortBy);
        List<ProposalDto> dtos = proposals.stream().map(ProposalMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/orders/{orderId}/select/{proposalId}")
    public ResponseEntity<Void> selectProposal(@PathVariable Long orderId,
                                               @PathVariable Long proposalId) {
        customerService.selectProposal(orderId, proposalId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/orders/{orderId}/start")
    public ResponseEntity<Void> markStarted(@PathVariable Long orderId) {
        customerService.markOrderStarted(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{orderId}/done")
    public ResponseEntity<Void> markDone(@PathVariable Long orderId) {
        customerService.markOrderDone(orderId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable Long orderId) {
        customerService.payOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/review")
    public ResponseEntity<Void> submitReview(@PathVariable Long orderId,
                                             @RequestParam int score,
                                             @RequestParam(required = false) String statement) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.submitReview(current.getId(), orderId, score, statement);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallet")
    public ResponseEntity<Long> getWalletBalance() {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        return ResponseEntity.ok(customerService.getWalletBalance(current.getId()));
    }

    @PostMapping("/wallet/charge")
    public ResponseEntity<Void> chargeWallet(@RequestParam Long amount) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.chargeWallet(current.getId(), amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody CustomerSignupDto dto) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.updateProfile(current.getId(), dto);
        return ResponseEntity.ok().build();
    }
}
