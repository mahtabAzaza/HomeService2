package service;

import entities.Review;

public interface ReviewService {

    //add review
    void addReview(Review review);
    //show reviews
    void showReview(Long orderId);

}
