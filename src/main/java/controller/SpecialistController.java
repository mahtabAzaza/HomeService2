package controller;

import DTO.OrderDto;
import DTO.ProposalDto;
import DTO.SpecialistSignupDto;
import entity.Order;
import entity.Proposal;
import entity.Specialist;
import mapper.OrderMapper;
import mapper.ProposalMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import security.UserContext;
import service.ProposalService;
import service.SpecialistService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/specialist")
public class SpecialistController {

    private final SpecialistService specialistService;
    private final ProposalService proposalService;

    public SpecialistController(SpecialistService specialistService,
                                ProposalService proposalService) {
        this.specialistService = specialistService;
        this.proposalService = proposalService;
    }

    @GetMapping("/orders/available")
    public ResponseEntity<List<OrderDto>> getAvailableOrders() {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        List<Order> orders = specialistService.getAvailableOrders(current.getId());
        List<OrderDto> dtos = orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


// ثبت پیشنهاد
    @PostMapping("/proposals")
    public ResponseEntity<Void> submitProposal(@RequestBody ProposalDto dto) {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        proposalService.submitProposal(
                current.getId(),
                dto.getOrderId(),
                dto.getProposalPrice(),
                dto.getStartDate(),
                dto.getDuration()
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{orderId}/start")
    public ResponseEntity<Void> markStarted(@PathVariable Long orderId) {
        specialistService.markOrderStarted(orderId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{orderId}/done")
    public ResponseEntity<Void> markDone(@PathVariable Long orderId) {
        specialistService.markOrderDone(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallet")
    public ResponseEntity<Long> getWalletBalance() {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        return ResponseEntity.ok(specialistService.getWalletBalance(current.getId()));
    }

    @PostMapping("/wallet/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam Long amount) {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        specialistService.withdraw(current.getId(), amount);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody SpecialistSignupDto dto) {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        specialistService.updateProfile(current.getId(), dto);
        return ResponseEntity.ok().build();
    }
}
