package service;

import entity.Service;
import repository.BaseRepository;
import java.util.List;
import java.util.Objects;

public class ServiceService {

    private final BaseRepository<Service, Long> serviceRepository;

    public ServiceService(BaseRepository<Service, Long> serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Add a new service  (by manager)
     */
    public void createService(Service service) {

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service cannot be null");
        }

        if (service.getServiceName() == null || service.getServiceName().isEmpty()) {
            throw new RuntimeException("Service name is required");
        }

        serviceRepository.save(service);
    }

    /**
     * Edit a service (by manager)
     */
    public void updateService(Service service) {

        Service existing = serviceRepository.findById(service.getServiceId());

        if (Objects.isNull(existing)) {
            throw new RuntimeException("Service not found");
        }

        existing.setServiceName(service.getServiceName());
        existing.setServiceDescription(service.getServiceDescription());
        existing.setServiceBasePrice(service.getServiceBasePrice());
        existing.setParentService(service.getParentService());

        serviceRepository.update(existing);
    }

    /**
     * Delete a service  (by manager)
     */
    public void deleteService(Long id) {

        Service service = serviceRepository.findById(id);

        if (Objects.isNull(service)) {
            throw new RuntimeException("Service not found");
        }

        serviceRepository.delete(service);
    }

    /**
     *Find all services
     */
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /**
     * Find a service
     */
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id);
    }
}




// void displayServiceTree() {
//
//    List<Service> services = serviceRepository.findAll();
//
//    for (Service service : services) {
//
//
//        if (service.getParentService() == null) {
//            printTree(service, 0);
//        }
//    }
//}