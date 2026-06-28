package service;

import entity.*;

import java.util.Objects;

public class ReviewService {

    private final BaseRepository<Review, Long> reviewRepository;
    private final BaseRepository<Order, Long> orderRepository;

    public ReviewService(BaseRepository<Review, Long> reviewRepository,
                         BaseRepository<Order, Long> orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * ثبت نظر مشتری برای سفارش انجام شده
     */
    public void addReview(Long orderId, int score, String statement) {

        // گرفتن سفارش
        Order order = orderRepository.findById(orderId);

        if (Objects.isNull(order)) {
            throw new RuntimeException("Order not found");
        }

        // فقط سفارش‌های پرداخت شده قابل review هستند
        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new RuntimeException("You can only review paid orders");
        }

        // کنترل امتیاز
        if (score < 1 || score > 5) {
            throw new RuntimeException("Rating must be between 1 to 5");
        }

        // ساخت Review واقعی مطابق Entity
        Review review = new Review();

        review.setOrder(order);                     // اتصال به سفارش
        review.setCustomer(order.getCustomer());    // از خود Order گرفته میشه
        review.setSpecialist(order.getSpecialist());// متخصص همان سفارش
        review.setScore(score);                   // امتیاز
        review.setStatement(statement);                 // متن نظر

        // ذخیره
        reviewRepository.save(review);
    }
}