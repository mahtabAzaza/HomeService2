package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.OrderHistoryDetailDto;
import ir.HomeServiceApplication.DTO.OrderHistoryDto;
import ir.HomeServiceApplication.DTO.OrderHistorySummaryDto;
import ir.HomeServiceApplication.DTO.UserFilterDto;
import ir.HomeServiceApplication.DTO.UserSearchResponseDto;
import ir.HomeServiceApplication.entity.Manager;
import ir.HomeServiceApplication.entity.Service;
import ir.HomeServiceApplication.entity.Specialist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManagerService {

    Manager login(String email, String password);

    // تایید ثبت ‌نام متخصص
    void approveSpecialist(Long specialistId);

    // حذف متخصص
    void deleteSpecialist(Long specialistId);

    // ایجاد خدمت اصلی (بدون والد)
    Service createService(String name, String description, Long basePrice);

    // افزودن زیرخدمت (با والد)
    void addSubService(Long parentId, String name, String description, Long basePrice);

    // ویرایش خدمت یا زیرخدمت
    void updateService(Long serviceId, String name, String description, Long basePrice);

    // حذف زیرخدمت
    void removeSubService(Long serviceId);

    // افزودن متخصص به زیرخدمت
    void addSpecialistToService(Long specialistId, Long serviceId);

    // حذف متخصص از زیرخدمت
    void removeSpecialistFromService(Long specialistId, Long serviceId);

    // مشاهده همه متخصصان
    List<Specialist> getAllSpecialists();

    // جستجو و فیلتر کاربران
    List<UserSearchResponseDto> searchUsers(UserFilterDto filter);

    // خلاصه تاریخچه سفارشات — جزئیات کامل فقط با getOrderHistoryDetail روی یک سفارش خاص
    Page<OrderHistorySummaryDto> orderHistory(OrderHistoryDto search, Pageable pageable);

    OrderHistoryDetailDto getOrderHistoryDetail(Long orderId);

}
