package service.serviceImpl;

import entity.*;
import exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.OrderRepository;
import repository.ProposalRepository;
import repository.ReviewRepository;
import repository.SpecialistRepository;
import service.ProposalService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final OrderRepository orderRepository;
    private final SpecialistRepository specialistRepository;
    private final ReviewRepository reviewRepository;

    public ProposalServiceImpl(ProposalRepository proposalRepository,
                               OrderRepository orderRepository,
                               SpecialistRepository specialistRepository,
                               ReviewRepository reviewRepository) {
        this.proposalRepository = proposalRepository;
        this.orderRepository = orderRepository;
        this.specialistRepository = specialistRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void submitProposal(Long specialistId, Long orderId, Long price,
                               LocalDateTime startDate, Integer duration) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (specialist.getStatus() != SpecialistStatus.APPROVED) {
            throw new NotApprovedException("Specialist is not approved");
        }

        if (order.getOrderStatus() != OrderStatus.WAITING_FOR_PROPOSAL &&
        order.getOrderStatus() != OrderStatus.WAITING_FOR_SELECTION) {
            throw new InvalidOperationException("Order is not accepting proposals");
        }

        // متخصص باید در حوزه خدمت این سفارش ثبت شده باشد
        boolean canHandle = specialist.getServices().contains(order.getService());
        if (!canHandle) {
            throw new InvalidOperationException("Specialist is not registered for this service category");
        }

        Proposal proposal = new Proposal();
        proposal.setSpecialist(specialist);
        proposal.setOrder(order);
        proposal.setProposalPrice(price);
        proposal.setStartDate(startDate);
        proposal.setDuration(duration);

        proposalRepository.save(proposal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proposal> getProposalsForOrder(Long orderId, String sortBy) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if ("score".equalsIgnoreCase(sortBy)) {
            List<Proposal> proposals = new ArrayList<>(proposalRepository.findByOrder(order));
            proposals.sort((a, b) -> {
                Double scoreA = reviewRepository.findAverageScoreBySpecialistId(a.getSpecialist().getId());
                Double scoreB = reviewRepository.findAverageScoreBySpecialistId(b.getSpecialist().getId());
                if (scoreA == null) scoreA = 0.0;
                if (scoreB == null) scoreB = 0.0;
                return Double.compare(scoreB, scoreA);
            });
            return proposals;
        }

        return proposalRepository.findByOrderOrderByProposalPriceAsc(order);
    }
}
