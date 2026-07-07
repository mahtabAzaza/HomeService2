
import ir.HomeServiceApplication.entity.*;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.ReviewRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.service.ReviewService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private SpecialistRepository specialistRepository;

    @InjectMocks
    private ReviewService reviewService;

    // =====================================================
    // ADD REVIEW
    // =====================================================

    // Saves a review when the order is in DONE status
    @Test
    void addReview_shouldSaveReview_whenOrderIsDone() {
        Customer customer = new Customer();
        Specialist specialist = new Specialist();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        reviewService.addReview(1L, 5, "Excellent");

        verify(reviewRepository).save(any(Review.class));
    }

    // Saves a review when the order is in PAID status
    @Test
    void addReview_shouldSaveReview_whenOrderIsPaid() {
        Customer customer = new Customer();
        Specialist specialist = new Specialist();

        Order order = new Order();
        order.setOrderStatus(OrderStatus.PAID);
        order.setCustomer(customer);
        order.setSpecialist(specialist);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        reviewService.addReview(1L, 3, null);

        verify(reviewRepository).save(any(Review.class));
    }

    // Throws when the order has not been completed or paid yet
    @Test
    void addReview_shouldThrowException_whenOrderNotDoneOrPaid() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> reviewService.addReview(1L, 4, "ok"));
    }

    // Throws when the review score is below the allowed minimum (0)
    @Test
    void addReview_shouldThrowException_whenScoreTooLow() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(new Customer());
        order.setSpecialist(new Specialist());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> reviewService.addReview(1L, 0, "bad"));
    }

    // Throws when the review score exceeds the allowed maximum (6)
    @Test
    void addReview_shouldThrowException_whenScoreTooHigh() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.DONE);
        order.setCustomer(new Customer());
        order.setSpecialist(new Specialist());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOperationException.class, () -> reviewService.addReview(1L, 6, "too high"));
    }

    // Throws when the given order ID does not exist in the repository
    @Test
    void addReview_shouldThrowException_whenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.addReview(99L, 5, "great"));
    }
}
