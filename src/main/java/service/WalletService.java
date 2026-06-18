package service;

import entities.Order;
import entities.OrderStatus;
import entities.Wallet;
import repository.BaseRepository;
import java.util.Objects;

public class WalletService {

    private final BaseRepository<Wallet, Long> walletRepository;
    private final BaseRepository<Order, Long> orderRepository;

    public WalletService(BaseRepository<Wallet, Long> walletRepository,
                         BaseRepository<Order, Long> orderRepository) {
        this.walletRepository = walletRepository;
        this.orderRepository = orderRepository;
    }

    /**
     *  نمایش موجودی کیف پول
     */
    public double showBalance(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId);

        if (Objects.isNull(wallet)) {
            throw new RuntimeException("Wallet not found");
        }

        return wallet.getBalance();
    }

    /**
     *  شارژ کیف پول (مشتری)
     */
    public void chargeWallet(Long walletId, double amount) {

        Wallet wallet = walletRepository.findById(walletId);

        if (Objects.isNull(wallet)) {
            throw new RuntimeException("Wallet not found");
        }

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }
// /////////////////////////?? change price datatypes///
        wallet.setBalance((long) (wallet.getBalance() + amount));
// / //////////////////////////////////////////////////
        walletRepository.update(wallet);
    }

    /**
     *  برداشت پول از کیف پول
     */
    public void withdrawMoney(Long walletId, double amount) {

        Wallet wallet = walletRepository.findById(walletId);

        if (Objects.isNull(wallet)) {
            throw new RuntimeException("Wallet not found");
        }

        if (amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
// ///////////////////////////////
        wallet.setBalance((long) (wallet.getBalance() - amount));
// /////////////////////////////
        walletRepository.update(wallet);
    }

    /**
     *  پرداخت سفارش
     *  کم کردن از مشتری
     *  اضافه کردن به متخصص
     */
    public void payForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        Wallet customerWallet = order.getCustomer().getWallet();
        Wallet specialistWallet = order.getSpecialist().getWallet();

        double price = order.getFinalPrice();

        if (customerWallet.getBalance() < price) {
            throw new RuntimeException("Not enough balance");
        }
// /////////////////////change datatype
        customerWallet.setBalance((long) (customerWallet.getBalance() - price));
        specialistWallet.setBalance((long) (specialistWallet.getBalance() + price));
// ///////////////////////
        walletRepository.update(customerWallet);
        walletRepository.update(specialistWallet);

        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.update(order);
    }
}