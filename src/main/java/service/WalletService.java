package service;




@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final OrderRepository orderRepository;

    public double showBalance(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException());

        return wallet.getBalance();
    }

    public void chargeWallet(Long walletId, double amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException());

        if (amount <= 0) {
            throw new InvalidOperationException();
        }

        wallet.setBalance(wallet.getBalance() + amount);
    }

    public void withdrawMoney(Long walletId, double amount) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException());

        if (amount <= 0) {
            throw new InvalidOperationException();
        }

        if (wallet.getBalance() < amount) {
            throw new InsufficientBalanceException();
        }

        wallet.setBalance(wallet.getBalance() - amount);
    }

    public void payForOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException());

        Wallet customerWallet = order.getCustomer().getWallet();
        Wallet specialistWallet = order.getSpecialist().getWallet();

        double price = order.getFinalPrice();

        if (customerWallet.getBalance() < price) {
            throw new InsufficientBalanceException();
        }

        customerWallet.setBalance(customerWallet.getBalance() - price);
        specialistWallet.setBalance(specialistWallet.getBalance() + price);

        order.setOrderStatus(OrderStatus.PAID);
    }
}