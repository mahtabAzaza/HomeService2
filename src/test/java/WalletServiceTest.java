
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

    @Test
    void showBalance_shouldReturnBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(750L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        Long result = walletService.showBalance(1L);

        assertEquals(750L, result);
    }

    @Test
    void showBalance_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.showBalance(99L));
    }

    // =====================================================
    // CHARGE WALLET
    // =====================================================

    @Test
    void chargeWallet_shouldIncreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.chargeWallet(1L, 200L);

        assertEquals(300L, wallet.getBalance());
    }

    @Test
    void chargeWallet_shouldThrowException_whenAmountNotPositive() {
        Wallet wallet = new Wallet();
        wallet.setBalance(100L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidOperationException.class, () -> walletService.chargeWallet(1L, 0L));
        assertThrows(InvalidOperationException.class, () -> walletService.chargeWallet(1L, -50L));
    }

    @Test
    void chargeWallet_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.chargeWallet(99L, 100L));
    }

    // =====================================================
    // WITHDRAW MONEY
    // =====================================================

    @Test
    void withdrawMoney_shouldDecreaseBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(500L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        walletService.withdrawMoney(1L, 200L);

        assertEquals(300L, wallet.getBalance());
    }

    @Test
    void withdrawMoney_shouldThrowException_whenInsufficientBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(50L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InsufficientBalanceException.class, () -> walletService.withdrawMoney(1L, 200L));
    }

    @Test
    void withdrawMoney_shouldThrowException_whenAmountNotPositive() {
        Wallet wallet = new Wallet();
        wallet.setBalance(500L);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(wallet));

        assertThrows(InvalidOperationException.class, () -> walletService.withdrawMoney(1L, 0L));
    }

    @Test
    void withdrawMoney_shouldThrowException_whenWalletNotFound() {
        when(walletRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.withdrawMoney(99L, 100L));
    }

    // =====================================================
    // PAY FOR ORDER
    // =====================================================

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

    @Test
    void payForOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> walletService.payForOrder(99L));
    }
}
