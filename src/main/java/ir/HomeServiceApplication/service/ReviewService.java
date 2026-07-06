package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.OrderStatus;
import ir.HomeServiceApplication.entity.Review;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.OrderRepository;
import ir.HomeServiceApplication.repository.ReviewRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final SpecialistRepository specialistRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository, SpecialistRepository specialistRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.specialistRepository = specialistRepository;

    }

    public void addReview(Long orderId, int score, String statement) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.DONE && order.getOrderStatus() != OrderStatus.PAID) {
            throw new InvalidOperationException("You can only review paid orders");
        }

        if (score < 1 || score > 5) {
            throw new InvalidOperationException("Rating must be between 1 to 5");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setCustomer(order.getCustomer());
        review.setSpecialist(order.getSpecialist());
        review.setScore(score);
        review.setStatement(statement);

        reviewRepository.save(review);
    }

    // ثبت امتیاز برای متخصص
 public void specialistRate (Long specialistId  ,Long orderId, int score  ) {
        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("specialist not found"));

 }

}
