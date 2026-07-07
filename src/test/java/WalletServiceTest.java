
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.InsufficientBalanceException;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.WalletRepository;
import ir.HomeServiceApplication.service.WalletService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock private WalletRepository walletRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private WalletService walletService;

    // =====================================================
    // SHOW BALANCE
    // =====================================================

    // Returns the current balance for a valid wallet ID
    @Test
    void showBalance_shouldReturnBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(750L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        Long result = walletService.showBalance(1L);

        assertEquals(750L, result);
    }

    // Throws when the given wallet ID does not exist in the repository
    @Test
    void showBalance_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.showBalance(99L));
    }

    // =====================================================
    // CHARGE WALLET
    // =====================================================

    // Adds the given amount to the existing wallet balance
    @Test
    void chargeWallet_shouldIncreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.chargeWallet(1L, 200L);

        assertEquals(300L, wallet.getBalance());
    }

    // Throws when the charge amount is zero or negative
    @Test
    void chargeWallet_shouldThrowException_whenAmountNotPositive() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidOperationException.class, () -> walletService.chargeWallet(1L, 0L));
        assertThrows(InvalidOperationException.class, () -> walletService.chargeWallet(1L, -50L));
    }

    // Throws when the given wallet ID does not exist in the repository
    @Test
    void chargeWallet_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.chargeWallet(99L, 100L));
    }

    // =====================================================
    // WITHDRAW MONEY
    // =====================================================

    // Subtracts the given amount from the existing wallet balance
    @Test
    void withdrawMoney_shouldDecreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(500L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.withdrawMoney(1L, 200L);

        assertEquals(300L, wallet.getBalance());
    }

    // Throws when the withdrawal amount is greater than the current wallet balance
    @Test
    void withdrawMoney_shouldThrowException_whenInsufficientBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(50L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientBalanceException.class, () -> walletService.withdrawMoney(1L, 200L));
    }

    // Throws when the withdrawal amount is zero or negative
    @Test
    void withdrawMoney_shouldThrowException_whenAmountNotPositive() {
        Wallet wallet = new Wallet();
        wallet.setBalance(500L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidOperationException.class, () -> walletService.withdrawMoney(1L, 0L));
    }

    // Throws when the given wallet ID does not exist in the repository
    @Test
    void withdrawMoney_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.withdrawMoney(99L, 100L));
    }

    // =====================================================
    // PAY FOR ORDER
    // =====================================================

    // Deducts the order price from the customer's wallet, credits the specialist, and marks the order PAID
    @Test
    void payForOrder_shouldTransferFundsAndMarkPaid() {
        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(1000L);

        Wallet specialistWallet = new Wallet();
        specialistWallet.setBalance(0L);

        Customer customer = new Customer();
        customer.setWallet(customerWallet);

        Specialist specialist = new Specialist();
        specialist.setWallet(specialistWallet);

        Order order = new Order();
        order.setFinalPrice(400L);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        walletService.payForOrder(1L);

        assertEquals(600L, customerWallet.getBalance());
        assertEquals(400L, specialistWallet.getBalance());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }

    // Throws when the customer's wallet balance is less than the order's final price
    @Test
    void payForOrder_shouldThrowException_whenInsufficientBalance() {
        Wallet customerWallet = new Wallet();
        customerWallet.setBalance(100L);

        Customer customer = new Customer();
        customer.setWallet(customerWallet);

        Specialist specialist = new Specialist();
        specialist.setWallet(new Wallet());

        Order order = new Order();
        order.setFinalPrice(500L);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InsufficientBalanceException.class, () -> walletService.payForOrder(1L));
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void payForOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.payForOrder(99L));
    }
}
