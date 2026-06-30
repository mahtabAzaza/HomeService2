package service;

import entity.Service;
import exception.InvalidOperationException;
import exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import repository.ServiceRepository;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public void createService(Service service) {

        if (service == null) {
            throw new InvalidOperationException("Service cannot be null");
        }

        if (service.getServiceName() == null || service.getServiceName().isBlank()) {
            throw new InvalidOperationException("Service name cannot be blank");
        }

        serviceRepository.save(service);
    }

    public void updateService(Service service) {

        Service existing = serviceRepository.findById(service.getId())
                .orElseThrow(() -> new NotFoundException("Service not found"));

        existing.setServiceName(service.getServiceName());
        existing.setServiceDescription(service.getServiceDescription());
        existing.setServiceBasePrice(service.getServiceBasePrice());
        existing.setParentService(service.getParentService());
    }

    public void deleteService(Long id) {

        if (!serviceRepository.existsById(id)) {
            throw new NotFoundException("Service not found");
        }

        serviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
    }
}
