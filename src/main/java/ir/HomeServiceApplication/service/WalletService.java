package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.OrderStatus;
import ir.HomeServiceApplication.entity.Wallet;
import ir.HomeServiceApplication.exception.InsufficientBalanceException;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.WalletRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;

    public Long showBalance(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        return wallet.getBalance();
    }

    public void chargeWallet(Long walletId, Long amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        if (amount <= 0) {
            throw new InvalidOperationException("Amount must be positive");
        }

        wallet.setBalance(wallet.getBalance() + amount);
    }

    public void withdrawMoney(Long walletId, Long amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        if (amount <= 0) {
            throw new InvalidOperationException("Amount must be positive");
        }

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Not enough balance in wallet");
        }

        wallet.setBalance(wallet.getBalance() - amount);
    }

    public void payForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Wallet customerWallet = order.getCustomer().getWallet();
        Wallet specialistWallet = order.getSpecialist().getWallet();
        Long price = order.getFinalPrice();

        if (customerWallet.getBalance() < price) {
            throw new InsufficientBalanceException("Not enough balance in wallet");
        }

        customerWallet.setBalance(customerWallet.getBalance() - price);
        specialistWallet.setBalance(specialistWallet.getBalance() + (price * 70 / 100));

        order.setOrderStatus(OrderStatus.PAID);
    }
}
