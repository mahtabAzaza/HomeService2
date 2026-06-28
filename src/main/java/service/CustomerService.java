package service;

import DTO.CustomerSignupDto;
import entity.Customer;
import entity.Order;
import entity.Proposal;
import entity.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomerService {

    // ثبت نام
    void signup(CustomerSignupDto dto);

    // ورود
    Customer login(String email, String password);

    // ویرایش اطلاعات کاربری
    void updateProfile(Long customerId, CustomerSignupDto dto);

    // مشاهده خدمات و زیرخدمات
    List<Service> getServices();

    // ثبت سفارش
    void placeOrder(Long customerId, Long serviceId, Long priceOffer,
                    LocalDateTime startDateTime, String address, String description);

    // مشاهده سفارش‌های مشتری
    List<Order> getMyOrders(Long customerId);

    // مشاهده پیشنهادهای یک سفارش
    List<Proposal> getProposalsForOrder(Long orderId);

    // انتخاب پیشنهاد متخصص
    void selectProposal(Long orderId, Long proposalId);

    // پرداخت سفارش از کیف پول
    void payOrder(Long orderId);

    // ثبت نظر پس از اتمام کار
    void submitReview(Long customerId, Long orderId, int score, String statement);

    // مشاهده موجودی کیف پول
    Long getWalletBalance(Long customerId);
}
