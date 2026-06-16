package service;

import entities.*;
import repository.BaseRepository;

import java.util.List;
import java.util.Objects;

public class ManagerService {

    private final BaseRepository<Specialist, Long> specialistRepository;
    private final BaseRepository<Service, Long> serviceRepository;

    public ManagerService(BaseRepository<Specialist, Long> specialistRepository,
                          BaseRepository<Service, Long> serviceRepository) {
        this.specialistRepository = specialistRepository;
        this.serviceRepository = serviceRepository;
    }

    /**
     * 📌 تایید متخصص توسط مدیر
     */
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (Objects.isNull(specialist)) {
            throw new RuntimeException("Specialist not found");
        }

        specialist.setStatus(SpecialistStatus.APPROVED);

        specialistRepository.update(specialist);
    }

    /**
     * 📌 ایجاد خدمت جدید
     */
    public void createService(Service service) {

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service cannot be null");
        }

        serviceRepository.save(service);
    }

    /**
     * 📌 حذف خدمت
     */
    public void deleteService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId);

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service not found");
        }

        serviceRepository.delete(service);
    }

    /**
     * 📌 مشاهده همه متخصص‌ها
     */
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    /**
     * 📌 مشاهده همه سرویس‌ها
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
}
    //manage services:
    // (add/delete specialist)

    // (add/delete service)

    // (edit service)

    //approve specialists

    //add/remove specialist

