package service;

import entity.Specialist;
import java.util.List;

public interface ManagerService {

    // تایید ثبت ‌نام متخصص
    void approveSpecialist(Long specialistId);

    // حذف متخصص
    void deleteSpecialist(Long specialistId);

    // ایجاد خدمت اصلی (بدون والد)
    void createService(String name, String description, Long basePrice);

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
}
