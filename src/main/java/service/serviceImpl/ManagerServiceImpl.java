package service.serviceImpl;

import entity.Service;
import entity.Specialist;
import entity.SpecialistStatus;
import exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.ServiceRepository;
import repository.SpecialistRepository;
import service.ManagerService;

import java.util.List;

@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final SpecialistRepository specialistRepository;
    private final ServiceRepository serviceRepository;

    public ManagerServiceImpl(SpecialistRepository specialistRepository,
                              ServiceRepository serviceRepository) {
        this.specialistRepository = specialistRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        specialist.setStatus(SpecialistStatus.APPROVED);
    }

    @Override
    public void deleteSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        specialistRepository.delete(specialist);
    }

    @Override
    public void createService(String name, String description, Long basePrice) {

        Service service = new Service();
        service.setServiceName(name);
        service.setServiceDescription(description);
        service.setServiceBasePrice(basePrice);
        service.setParentService(null);

        serviceRepository.save(service);
    }

    @Override
    public void addSubService(Long parentId, String name, String description, Long basePrice) {

        Service parent = serviceRepository.findById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent service not found"));

        // نام زیرخدمت نباید تکراری در همان والد باشد
        List<Service> siblings = serviceRepository.findByParentService(parent);
        boolean duplicate = siblings.stream()
                .anyMatch(s -> s.getServiceName().equals(name));

        if (duplicate) {
            throw new InvalidOperationException("A sub-service with this name already exists under the same parent");
        }

        Service subService = new Service();
        subService.setServiceName(name);
        subService.setServiceDescription(description);
        subService.setServiceBasePrice(basePrice);
        subService.setParentService(parent);

        serviceRepository.save(subService);
    }

    @Override
    public void updateService(Long serviceId, String name, String description, Long basePrice) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        service.setServiceName(name);
        service.setServiceDescription(description);
        service.setServiceBasePrice(basePrice);
    }

    @Override
    public void removeSubService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        serviceRepository.delete(service);
    }

    @Override
    public void addSpecialistToService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        if (!specialist.getServices().contains(service)) {
            specialist.getServices().add(service);
        }
    }

    @Override
    public void removeSpecialistFromService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        specialist.getServices().remove(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }
}
