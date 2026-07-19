package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.DTO.OrderDto;
import ir.HomeServiceApplication.DTO.PlaceOrderRequest;
import ir.HomeServiceApplication.DTO.ProposalDto;
import jakarta.validation.Valid;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.mapper.OrderMapper;
import ir.HomeServiceApplication.mapper.ProposalMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ir.HomeServiceApplication.security.UserContext;
import ir.HomeServiceApplication.service.CustomerService;

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
    public ResponseEntity<Void> placeOrder(@Valid @RequestBody PlaceOrderRequest dto) {
        customerService.placeOrder(
                UserContext.getCurrentEmail(),
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
            @RequestParam(defaultValue = "price") ProposalSortByType sortBy) {
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
    public ResponseEntity<?> payOrder(@PathVariable Long orderId) {
        customerService.payOrder(orderId);
        return ResponseEntity.ok("Payment successful");
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
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody CustomerSignupDto dto) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.updateProfile(current.getId(), dto);
        return ResponseEntity.ok().build();
    }
}
