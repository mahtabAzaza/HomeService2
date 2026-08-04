package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.*;
import jakarta.validation.Valid;
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.mapper.OrderMapper;
import ir.HomeServiceApplication.mapper.PaymentMapper;
import ir.HomeServiceApplication.mapper.ProposalMapper;
import ir.HomeServiceApplication.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ir.HomeServiceApplication.security.UserContext;
import ir.HomeServiceApplication.service.CustomerService;
import ir.HomeServiceApplication.service.ServiceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceService serviceService;
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public CustomerController(CustomerService customerService, ServiceService serviceService,
                              PaymentService paymentService, PaymentMapper paymentMapper) {
        this.customerService = customerService;
        this.serviceService = serviceService;
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @GetMapping("/services")
    public ResponseEntity<Page<ServiceResponseDto>> getServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(serviceService.getAllParentServices(pageable));
    }

@PostMapping("/orders")
public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody PlaceOrderRequest dto) {

    Order order = customerService.placeOrder(
            UserContext.getCurrentEmail(),
            dto.getServiceId(),
            dto.getPriceOffer(),
            dto.getOrderStartDateTime(),
            dto.getAddress(),
            dto.getOrderDescription()
    );

    return ResponseEntity.ok(OrderMapper.toDto(order));
}

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(
            @RequestParam(required = false) OrderStatus status) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        List<Order> orders = customerService.getMyOrders(current.getId(), status);
        List<OrderDto> dtos = orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/orders/{orderId}/proposals")
    public ResponseEntity<List<ProposalDto>> getProposals(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "PRICE") ProposalSortByType sortBy) {
        List<Proposal> proposals = customerService.getProposalsForOrder(orderId, sortBy);
        List<ProposalDto> dtos = proposals.stream().map(ProposalMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/orders/{orderId}/select/{proposalId}")
    public ResponseEntity<String> selectProposal(@PathVariable Long orderId,
                                               @PathVariable Long proposalId) {
        customerService.selectProposal(orderId, proposalId);
        return ResponseEntity.ok("Proposal selected successfully");
    }
    @PutMapping("/orders/{orderId}/start")
    public ResponseEntity<String> markStarted(@PathVariable Long orderId) {
        customerService.markOrderStarted(orderId);
        return ResponseEntity.ok("Order marked as started");
    }

    @PutMapping("/orders/{orderId}/done")
    public ResponseEntity<String> markDone(@PathVariable Long orderId) {
        customerService.markOrderDone(orderId);
        return ResponseEntity.ok("Order marked as done");
    }



    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId) {
        customerService.payOrder(orderId);
        return ResponseEntity.ok("Payment successful");
    }

    @PostMapping("/orders/{orderId}/review")
    public ResponseEntity<String> submitReview(@PathVariable Long orderId,
                                             @RequestParam int score,
                                             @RequestParam(required = false) String statement) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.submitReview(current.getId(), orderId, score, statement);
        return ResponseEntity.ok("Review submitted successfully");
    }

    @GetMapping("/wallet")
    public ResponseEntity<Long> getWalletBalance() {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        return ResponseEntity.ok(customerService.getWalletBalance(current.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody CustomerSignupDto dto) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        customerService.updateProfile(current.getId(), dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/charge-wallet")
    public ResponseEntity<PaymentLinkResponseDto> chargeWallet(@RequestParam Long amount) {
        Customer current = customerService.findByEmail(UserContext.getCurrentEmail());
        PaymentSession session = paymentService.createChargeSession(current.getId(), amount);
        return ResponseEntity.ok(paymentMapper.toLinkDto(session));
    }

    @GetMapping("/payments/{token}")
    public ResponseEntity<PaymentSessionResponseDto> getPaymentSession(@PathVariable String token) {
        return ResponseEntity.ok(paymentMapper.toResponse(paymentService.findByToken(token)));
    }

    @PostMapping("/payments/{token}")
    public ResponseEntity<PaymentResultDto> pay(@PathVariable String token,
                                                @Valid @RequestBody PaymentRequestDto dto) {
        PaymentSession session = paymentService.pay(token, dto);
        return ResponseEntity.ok(new PaymentResultDto(
                "Payment successful. Your wallet has been charged.",
                session.getStatus(),
                session.getAmount()
        ));
    }
}
