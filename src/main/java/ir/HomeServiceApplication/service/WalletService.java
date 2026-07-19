package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.OrderStatus;
import ir.HomeServiceApplication.entity.Wallet;
import ir.HomeServiceApplication.entity.WalletTransaction;
import ir.HomeServiceApplication.exception.InsufficientBalanceException;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.WalletRepository;
import ir.HomeServiceApplication.repository.WalletTransactionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    private void record(Wallet wallet, Long amount, String type, String description) {
        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setDescription(description);
        walletTransactionRepository.save(tx);
    }

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
        record(wallet, amount, "CREDIT", "Wallet charge");
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
        record(wallet, amount, "DEBIT", "Withdrawal");
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

        record(customerWallet, price, "DEBIT", "Order payment");
        record(specialistWallet, price * 70 / 100, "CREDIT", "Order payment received");

        order.setOrderStatus(OrderStatus.PAID);
    }
}
