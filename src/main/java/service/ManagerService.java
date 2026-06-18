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
     *approve specialist by manager
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
     * add a  new service
     */
    public void createService(Service service) {

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service cannot be null");
        }

        serviceRepository.save(service);
    }

    /**
     *Delete a service
     */
    public void deleteService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId);

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service not found");
        }

        serviceRepository.delete(service);
    }

    /**
     * اSee all specialists
     */
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    /**
     *see all services
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

