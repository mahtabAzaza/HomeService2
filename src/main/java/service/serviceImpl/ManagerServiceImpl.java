package service.serviceImpl;

import entity.Service;
import entity.Specialist;
import entity.SpecialistStatus;
import repository.ServiceRepository;
import repository.SpecialistRepository;
import service.ManagerService;



public class ManagerServiceImpl implements ManagerService {

    private final SpecialistRepository specialistRepository;
    private final ServiceRepository serviceRepository;

    public ManagerServiceImpl(SpecialistRepository specialistRepository,
                              ServiceRepository serviceRepository) {
        this.specialistRepository = specialistRepository;
        this.serviceRepository = serviceRepository;
    }

    // 1. تایید متخصص
    @Override
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId);

        if (specialist == null) {
            throw new RuntimeException("Specialist not found");
        }

        specialist.setStatus(SpecialistStatus.APPROVED);

        specialistRepository.update(specialist);
    }

    // 2. افزودن زیرخدمت
    @Override
    public void addSubService(Service service) {

        if (service.getParentService() == null) {
            throw new RuntimeException("Parent service is required");
        }

        Service parent = serviceRepository.findById(service.getParentService().getServiceId());

        if (parent == null) {
            throw new RuntimeException("Parent service not found");
        }

        // جلوگیری از تکرار نام زیر همان والد
        for (Service child : parent.getChildServices()) {
            if (child.getServiceName().equals(service.getServiceName())) {
                throw new RuntimeException("Duplicate sub service name under same parent");
            }
        }

        serviceRepository.save(service);
    }

    // 3. ویرایش خدمت
    @Override
    public void updateService(Service service) {

        Service existing = serviceRepository.findById(service.getServiceId());

        if (existing == null) {
            throw new RuntimeException("Service not found");
        }

        existing.setServiceName(service.getServiceName());
        existing.setServiceBasePrice(service.getServiceBasePrice());
        existing.setServiceDescription(service.getServiceDescription());

        serviceRepository.update(existing);
    }

    // 4. حذف زیرخدمت
    @Override
    public void removeSubService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId);

        if (service == null) {
            throw new RuntimeException("Service not found");
        }

        serviceRepository.delete(service);
    }

    // 5. افزودن متخصص به زیرخدمت
    @Override
    public void addSpecialistToService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId);
        Service service = serviceRepository.findById(serviceId);

        if (specialist == null || service == null) {
            throw new RuntimeException("Specialist or Service not found");
        }

        if (!service.getSpecialists().contains(specialist)) {
            service.getSpecialists().add(specialist);
        }

        serviceRepository.update(service);
    }

    // 6. حذف متخصص از زیرخدمت
    @Override
    public void removeSpecialistFromService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId);
        Service service = serviceRepository.findById(serviceId);

        if (specialist == null || service == null) {
            throw new RuntimeException("Specialist or Service not found");
        }

        service.getSpecialists().remove(specialist);

        serviceRepository.update(service);
    }
}