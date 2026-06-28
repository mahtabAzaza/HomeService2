package service;

import DTO.SpecialistSignupDto;
import entity.Order;
import entity.Specialist;

import java.util.List;

public interface SpecialistService {

    // ثبت نام
    void signup(SpecialistSignupDto dto);

    // ورود
    Specialist login(String email, String password);

    // ویرایش اطلاعات (وضعیت دوباره به انتظار تایید برمی‌گردد)
    void updateProfile(Long specialistId, SpecialistSignupDto dto);

    // مشاهده سفارش‌های قابل پیشنهاد (در حوزه تخصص متخصص)
    List<Order> getAvailableOrders(Long specialistId);

    // اعلام شروع کار
    void markOrderStarted(Long orderId);

    // اعلام پایان کار
    void markOrderDone(Long orderId);

    // مشاهده موجودی کیف پول
    Long getWalletBalance(Long specialistId);

    // برداشت از کیف پول
    void withdraw(Long specialistId, Long amount);
}
