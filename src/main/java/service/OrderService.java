package service;
import entity.*;
import repository.BaseRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrderService {

    private final BaseRepository<Order, Long> orderRepository;
    private final BaseRepository<Proposal, Long> proposalRepository;
    private final BaseRepository<Wallet, Long> walletRepository;

    public OrderService(BaseRepository<Order, Long> orderRepository,
                        BaseRepository<Proposal, Long> proposalRepository,
                        BaseRepository<Wallet, Long> walletRepository) {
        this.orderRepository = orderRepository;
        this.proposalRepository = proposalRepository;
        this.walletRepository = walletRepository;
    }

    // 1) ثبت سفارش توسط مشتری
    public void addOrder(Long customerId, Long serviceId, Order order) {

        Customer customer = order.getCustomer(); // فرض ساده
        Service service = order.getService();    // فرض ساده

        if (Objects.isNull(customer) || Objects.isNull(service)) {
            throw new RuntimeException("Invalid data");
        }

        if (order.getPriceOffer() < service.getServiceBasePrice()) {
            throw new RuntimeException("Price must be >= base price");
        }

        order.setOrderStartDateTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);

        orderRepository.save(order);
    }

    // 2) نمایش پیشنهادها برای یک سفارش
    public List<Proposal> showProposals(Long orderId) {

        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        return order.getProposals(); // فرض: لیست داخل Order
    }

    // 3) انتخاب متخصص از روی پیشنهاد
    public void chooseSpecialist(Long orderId, Long proposalId) {

        Order order = orderRepository.findById(orderId);
        Proposal proposal = proposalRepository.findById(proposalId);

        if (Objects.isNull(order) || Objects.isNull(proposal)) {
            throw new RuntimeException("Not found");
        }

        order.setSpecialist(proposal.getSpecialist());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        orderRepository.update(order);
    }

    // 4) اعلام پایان کار توسط متخصص
    public void markOrderDone(Long orderId) {

        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        order.setOrderStatus(OrderStatus.DONE);

        orderRepository.update(order);
    }

    // 5) پرداخت توسط مشتری
    public void pay(Long orderId) {

        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        Wallet customerWallet = order.getCustomer().getWallet();
        Wallet specialistWallet = order.getSpecialist().getWallet();

        if (customerWallet.getBalance() < order.getFinalPrice()) {
            throw new RuntimeException("Insufficient balance");
        }

        customerWallet.setBalance(customerWallet.getBalance() - order.getFinalPrice());
        specialistWallet.setBalance(specialistWallet.getBalance() + order.getFinalPrice());

        walletRepository.update(customerWallet);
        walletRepository.update(specialistWallet);

        order.setOrderStatus(OrderStatus.PAID);

        orderRepository.update(order);
    }
}