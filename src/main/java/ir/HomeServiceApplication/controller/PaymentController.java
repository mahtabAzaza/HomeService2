package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.PaymentRequestDto;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.OrderStatus;
import ir.HomeServiceApplication.exception.NotFoundException;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {

    private final WalletService walletService;
    private final OrderRepository orderRepository;

    public PaymentController(WalletService walletService, OrderRepository orderRepository) {
        this.walletService = walletService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/wallet/charge")
    public ResponseEntity<String> chargeWallet(
            @RequestBody PaymentRequestDto dto,
            HttpSession session) {

        String savedCaptcha = (String) session.getAttribute("captcha");

        if (savedCaptcha == null) {
            return ResponseEntity.badRequest().body("Captcha expired.");
        }

        if (!savedCaptcha.equalsIgnoreCase(dto.getCaptcha())) {
            return ResponseEntity.badRequest().body("Invalid captcha.");
        }

        session.removeAttribute("captcha");

        if (dto.getOrderId() == null) {
            return ResponseEntity.badRequest().body("Missing order ID.");
        }

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DONE) {
            return ResponseEntity.badRequest().body("Order is not in DONE status.");
        }

        Long price = order.getFinalPrice() != null ? order.getFinalPrice() : order.getPriceOffer();

        Long customerWalletId = order.getCustomer().getWallet().getId();

        // شارژ کیف پول مشتری از طریق کارت
        walletService.chargeWallet(customerWalletId, price);

        // پرداخت به متخصص از کیف پول مشتری
        walletService.payForOrder(order.getId());

        return ResponseEntity.ok("Payment successful. Order is now PAID.");
    }
}
