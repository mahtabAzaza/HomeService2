package controller;

import DTO.ServiceDto;
import DTO.SpecialistResponseDto;
import entity.Specialist;
import mapper.SpecialistMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ManagerService;
import service.ServiceService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;
    private final ServiceService serviceService;

    public ManagerController(ManagerService managerService, ServiceService serviceService) {
        this.managerService = managerService;
        this.serviceService = serviceService;
    }

    // --- Specialist management ---

    @GetMapping("/specialists")
    public ResponseEntity<List<SpecialistResponseDto>> getAllSpecialists() {
        List<Specialist> specialists = managerService.getAllSpecialists();
        List<SpecialistResponseDto> dtos = specialists.stream()
                .map(SpecialistMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/specialists/{id}/approve")
    public ResponseEntity<Void> approveSpecialist(@PathVariable Long id) {
        managerService.approveSpecialist(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/specialists/{id}")
    public ResponseEntity<Void> deleteSpecialist(@PathVariable Long id) {
        managerService.deleteSpecialist(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/services/{serviceId}/specialists/{specialistId}")
    public ResponseEntity<Void> addSpecialistToService(@PathVariable Long serviceId,
                                                       @PathVariable Long specialistId) {
        managerService.addSpecialistToService(specialistId, serviceId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/services/{serviceId}/specialists/{specialistId}")
    public ResponseEntity<Void> removeSpecialistFromService(@PathVariable Long serviceId,
                                                            @PathVariable Long specialistId) {
        managerService.removeSpecialistFromService(specialistId, serviceId);
        return ResponseEntity.ok().build();
    }

    // --- Service management ---

    @GetMapping("/services")
    public ResponseEntity<List<entity.Service>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    @PostMapping("/services")
    public ResponseEntity<Void> createService(@RequestBody ServiceDto dto) {
        managerService.createService(dto.getServiceName(), dto.getServiceDescription(), dto.getServiceBasePrice());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/services/{parentId}/subservices")
    public ResponseEntity<Void> addSubService(@PathVariable Long parentId,
                                              @RequestBody ServiceDto dto) {
        managerService.addSubService(parentId, dto.getServiceName(), dto.getServiceDescription(), dto.getServiceBasePrice());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<Void> updateService(@PathVariable Long id,
                                              @RequestBody ServiceDto dto) {
        managerService.updateService(id, dto.getServiceName(), dto.getServiceDescription(), dto.getServiceBasePrice());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        managerService.removeSubService(id);
        return ResponseEntity.ok().build();
    }
}
