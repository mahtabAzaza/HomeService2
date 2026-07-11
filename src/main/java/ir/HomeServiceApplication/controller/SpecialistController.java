package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.OrderDto;
import ir.HomeServiceApplication.DTO.ProposalDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import jakarta.validation.Valid;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.mapper.OrderMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ir.HomeServiceApplication.security.UserContext;
import ir.HomeServiceApplication.service.ProposalService;
import ir.HomeServiceApplication.service.SpecialistService;

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

    // مشاهده سفارشات موجود
    @GetMapping("/orders/available")
    public ResponseEntity<List<OrderDto>> getAvailableOrders() {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        List<Order> orders = specialistService.getAvailableOrders(current.getId());
        List<OrderDto> dtos = orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // تاریخچه سفارشاتی که متخصص برای آن‌ها پیشنهاد داده
    @GetMapping("/orders/history")
    public ResponseEntity<List<OrderDto>> getOrderHistory() {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        List<Order> orders = specialistService.getOrderHistory(current.getId());
        List<OrderDto> dtos = orders.stream().map(OrderMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }


    // ثبت پیشنهاد
    @PostMapping("/proposals")
    public ResponseEntity<Void> submitProposal(@Valid @RequestBody ProposalDto dto) {
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


     // موجودی
    @GetMapping("/wallet")
    public ResponseEntity<Long> getWalletBalance() {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        return ResponseEntity.ok(specialistService.getWalletBalance(current.getId()));
    }

    // برداشت
    @PostMapping("/wallet/withdraw")
    public ResponseEntity<Void> withdraw(@RequestParam Long amount) {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        specialistService.withdraw(current.getId(), amount);
        return ResponseEntity.ok().build();
    }

    // ویرایش اطلاعات
    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@Valid @RequestBody SpecialistSignupDto dto) {
        Specialist current = specialistService.findByEmail(UserContext.getCurrentEmail());
        specialistService.updateProfile(current.getId(), dto);
        return ResponseEntity.ok().build();
    }
}
