package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.entity.Order;

import java.util.List;

public interface OrderService {

    // مشاهده سفارش‌های یک مشتری
    List<Order> getOrdersByCustomer(Long customerId);

    // مشاهده سفارش‌های یک متخصص
    List<Order> getOrdersBySpecialist(Long specialistId);
}
