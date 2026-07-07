
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotApprovedException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.ProposalRepository;
import ir.HomeServiceApplication.repository.ReviewRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.service.serviceImpl.ProposalServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProposalServiceImplTest {

    @Mock private ProposalRepository proposalRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private SpecialistRepository specialistRepository;
    @Mock private ReviewRepository reviewRepository;

    @InjectMocks
    private ProposalServiceImpl proposalService;

    // =====================================================
    // SUBMIT PROPOSAL
    // =====================================================

    // Saves a proposal when the order is in WAITING_FOR_PROPOSAL status and the specialist is registered for the service
    @Test
    void submitProposal_shouldSaveProposal_whenOrderIsWaitingForProposal() {
        Service service = new Service();

        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setServices(new ArrayList<>(List.of(service)));

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);
        order.setService(service);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(i -> i.getArgument(0));

        proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now().plusDays(1), 3);

        verify(proposalRepository).save(any(Proposal.class));
    }

    // Saves a proposal when the order is in WAITING_FOR_SELECTION status (already has at least one proposal)
    @Test
    void submitProposal_shouldSaveProposal_whenOrderIsWaitingForSelection() {
        Service service = new Service();

        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setServices(new ArrayList<>(List.of(service)));

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_SELECTION);
        order.setService(service);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(i -> i.getArgument(0));

        proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now().plusDays(1), 3);

        verify(proposalRepository).save(any(Proposal.class));
    }

    // Throws when the given specialist ID does not exist in the repository
    @Test
    void submitProposal_shouldThrowException_whenSpecialistNotFound() {
        when(specialistRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now(), 3));
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void submitProposal_shouldThrowException_whenOrderNotFound() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now(), 3));
    }

    // Throws when the specialist account is still awaiting manager approval
    @Test
    void submitProposal_shouldThrowException_whenSpecialistNotApproved() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));

        assertThrows(NotApprovedException.class,
                () -> proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now(), 3));
    }

    // Throws when the order is IN_PROGRESS and no longer accepts new proposals
    @Test
    void submitProposal_shouldThrowException_whenOrderNotAcceptingProposals() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setServices(new ArrayList<>());

        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        order.setService(new Service());

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class,
                () -> proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now(), 3));
    }

    // Throws when the specialist is not registered for the service the order belongs to
    @Test
    void submitProposal_shouldThrowException_whenSpecialistNotRegisteredForService() {
        Service orderService = new Service();

        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.APPROVED);
        specialist.setServices(new ArrayList<>());  // empty — not registered

        Order order = new Order();
        order.setOrderStatus(OrderStatus.WAITING_FOR_PROPOSAL);
        order.setService(orderService);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class,
                () -> proposalService.submitProposal(1L, 1L, 500L, LocalDateTime.now(), 3));
    }

    // =====================================================
    // GET PROPOSALS FOR ORDER
    // =====================================================

    // Returns proposals for the order sorted by price ascending
    @Test
    void getProposalsForOrder_sortByPrice_shouldReturnList() {
        Order order = new Order();
        List<Proposal> proposals = List.of(new Proposal());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findByOrderOrderByProposalPriceAsc(order)).thenReturn(proposals);

        List<Proposal> result = proposalService.getProposalsForOrder(1L, "price");

        assertEquals(1, result.size());
    }

    // Returns proposals sorted by specialist average review score, highest first
    @Test
    void getProposalsForOrder_sortByScore_shouldReturnHighestScoreFirst() {
        Order order = new Order();

        Specialist specA = new Specialist();
        specA.setId(1L);
        Specialist specB = new Specialist();
        specB.setId(2L);

        Proposal proposalA = new Proposal();
        proposalA.setSpecialist(specA);  // score 2.0 — lower

        Proposal proposalB = new Proposal();
        proposalB.setSpecialist(specB);  // score 4.5 — higher

        List<Proposal> unsorted = new ArrayList<>(List.of(proposalA, proposalB));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findByOrder(order)).thenReturn(unsorted);
        when(reviewRepository.findAverageScoreBySpecialistId(1L)).thenReturn(2.0);
        when(reviewRepository.findAverageScoreBySpecialistId(2L)).thenReturn(4.5);

        List<Proposal> result = proposalService.getProposalsForOrder(1L, "score");

        assertEquals(proposalB, result.get(0));
        assertEquals(proposalA, result.get(1));
    }

    // Treats a null average score as 0 so specialists with no reviews rank last when sorting by score
    @Test
    void getProposalsForOrder_sortByScore_shouldHandleNullScoreAsZero() {
        Order order = new Order();

        Specialist specA = new Specialist();
        specA.setId(1L);
        Specialist specB = new Specialist();
        specB.setId(2L);

        Proposal proposalA = new Proposal();
        proposalA.setSpecialist(specA);  // null score → treated as 0

        Proposal proposalB = new Proposal();
        proposalB.setSpecialist(specB);  // score 3.0

        List<Proposal> unsorted = new ArrayList<>(List.of(proposalA, proposalB));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(proposalRepository.findByOrder(order)).thenReturn(unsorted);
        when(reviewRepository.findAverageScoreBySpecialistId(1L)).thenReturn(null);
        when(reviewRepository.findAverageScoreBySpecialistId(2L)).thenReturn(3.0);

        List<Proposal> result = proposalService.getProposalsForOrder(1L, "score");

        assertEquals(proposalB, result.get(0));
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void getProposalsForOrder_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> proposalService.getProposalsForOrder(99L, "price"));
    }
}
