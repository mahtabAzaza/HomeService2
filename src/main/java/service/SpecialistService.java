package service;

import entities.*;
import repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class SpecialistService {

    private final BaseRepository<Specialist, Long> specialistRepository;
    private final BaseRepository<Service, Long> serviceRepository;

    public SpecialistService(BaseRepository<Specialist, Long> specialistRepository,
                             BaseRepository<Service, Long> serviceRepository) {
        this.specialistRepository = specialistRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * 📌 ویرایش اطلاعات متخصص
     * بعد از تغییر، وضعیت برمی‌گرده به WAITING_FOR_APPROVAL
     */
    public void updateSpecialist(Long specialistId, Specialist updated) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (Objects.isNull(specialist)) {
            throw new RuntimeException("Specialist not found");
        }

        specialist.setName(updated.getName());
        specialist.setEmail(updated.getEmail());
        specialist.setPassword(updated.getPassword());
        specialist.setProfileImage(updated.getProfileImage());

        // 📌 بعد از تغییر اطلاعات باید دوباره تایید شود
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        specialistRepository.update(specialist);
    }

    /**
     * 📌 اضافه کردن تخصص (service) به متخصص
     */
    public void addServiceToSpecialist(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId);
        Service service = serviceRepository.findById(serviceId);

        if (Objects.isNull(specialist) || Objects.isNull(service)) {
            throw new RuntimeException("Not found");
        }

        specialist.getServices().add(service);

        specialistRepository.update(specialist);
    }

    /**
     * 📌 مشاهده وضعیت حساب
     */
    public SpecialistStatus getStatus(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (Objects.isNull(specialist)) {
            throw new RuntimeException("Specialist not found");
        }

        return specialist.getStatus();
    }

    /**
     * 📌 مشاهده همه متخصص‌های یک سرویس
     */
    public List<Specialist> getSpecialistsByService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId);

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service not found");
        }

        return service.getSpecialists();
    }
}