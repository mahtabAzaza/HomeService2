package service;

import entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProposalService {

    private final BaseRepository<Proposal, Long> proposalRepository;
    private final BaseRepository<Order, Long> orderRepository;
    private final BaseRepository<Specialist, Long> specialistRepository;

    public ProposalService(BaseRepository<Proposal, Long> proposalRepository,
                           BaseRepository<Order, Long> orderRepository,
                           BaseRepository<Specialist, Long> specialistRepository) {
        this.proposalRepository = proposalRepository;
        this.orderRepository = orderRepository;
        this.specialistRepository = specialistRepository;
    }

    /**
     *  متخصص سفارش‌های قابل پیشنهاد را می‌بیند
     * (فقط سفارش‌هایی که هنوز در حالت WAITING_FOR_PROPOSAL هستند)
     */
    public List<Order> showAvailableOrders() {
        return orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.WAITING_FOR_PROPOSAL)
                .collect(Collectors.toList());
    }

    /**
     *  متخصص برای یک سفارش پیشنهاد ثبت می‌کند
     * شامل:
     * - قیمت پیشنهادی
     * - زمان شروع
     * - مدت زمان انجام
     * - زمان ایجاد پیشنهاد (سیستمی)
     */
    public void addProposal(Long specialistId,
                         Long orderId,
                         double price,
                         LocalDateTime suggestedStartTime,
                         int durationHours) {

        Specialist specialist = specialistRepository.findById(specialistId);
        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(specialist) || Objects.isNull(order)) {
            throw new RuntimeException("Specialist or Order not found");
        }

        // متخصص باید تایید شده باشد
        if (specialist.getStatus() != SpecialistStatus.APPROVED) {
            throw new RuntimeException("Specialist is not approved");
        }

        Proposal proposal = new Proposal();

        proposal.setSpecialist(specialist);
        proposal.setOrder(order);
        proposal.setProposalPrice((long) price);
        proposal.setStartDate(suggestedStartTime);
        proposal.setDuration(durationHours);

        //  زمان ثبت پیشنهاد توسط سیستم
        proposal.setProposalRegistrationDate(LocalDateTime.now());

        proposalRepository.save(proposal);
    }

    /**
     *  نمایش همه پیشنهادها برای یک سفارش (برای مشتری)
     */
    public List<Proposal> showProposals(Long orderId) {

        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        return order.getProposals();
    }

    /**
     *  انتخاب یک پیشنهاد توسط مشتری
     * اینجا متخصص نهایی برای سفارش مشخص می‌شود
     */
    public void chooseProposal(Long orderId, Long proposalId) {

        Order order = orderRepository.findById(orderId);
        Proposal proposal = proposalRepository.findById(proposalId);

        if (Objects.isNull(order) || Objects.isNull(proposal)) {
            throw new RuntimeException("Order or Proposal not found");
        }

        order.setSpecialist(proposal.getSpecialist());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        orderRepository.update(order);
    }
}