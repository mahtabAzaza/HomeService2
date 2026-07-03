package service;

import entity.Order;
import entity.OrderStatus;
import entity.Review;
import exception.InvalidOperationException;
import exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.OrderRepository;
import repository.ReviewRepository;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
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


}
