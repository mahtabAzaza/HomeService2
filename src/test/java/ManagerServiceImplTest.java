
import entity.Service;
import entity.Specialist;
import entity.SpecialistStatus;
import exception.InvalidOperationException;
import exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ServiceRepository;
import repository.SpecialistRepository;
import service.serviceImpl.ManagerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManagerServiceImplTest {

    @Mock private SpecialistRepository specialistRepository;
    @Mock private ServiceRepository serviceRepository;

    @InjectMocks
    private ManagerServiceImpl managerService;

    // =====================================================
    // APPROVE SPECIALIST
    // =====================================================

    @Test
    void approveSpecialist_shouldSetStatusToApproved() {
        Specialist specialist = new Specialist();
        specialist.setStatus(SpecialistStatus.WAITING_FOR_APPROVAL);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        managerService.approveSpecialist(1L);

        assertEquals(SpecialistStatus.APPROVED, specialist.getStatus());
    }

    @Test
    void approveSpecialist_shouldThrowException_whenNotFound() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> managerService.approveSpecialist(99L));
    }

    // =====================================================
    // DELETE SPECIALIST
    // =====================================================

    @Test
    void deleteSpecialist_shouldDeleteSpecialist() {
        Specialist specialist = new Specialist();

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));

        managerService.deleteSpecialist(1L);

        verify(specialistRepository).delete(specialist);
    }

    @Test
    void deleteSpecialist_shouldThrowException_whenNotFound() {
        when(specialistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> managerService.deleteSpecialist(99L));
    }

    // =====================================================
    // CREATE SERVICE
    // =====================================================

    @Test
    void createService_shouldSaveService() {
        when(serviceRepository.save(any(Service.class))).thenAnswer(i -> i.getArgument(0));

        managerService.createService("Plumbing", "Fix pipes", 100L);

        verify(serviceRepository).save(any(Service.class));
    }

    // =====================================================
    // ADD SUB-SERVICE
    // =====================================================

    @Test
    void addSubService_shouldSaveSubService() {
        Service parent = new Service();
        parent.setServiceName("Home Services");

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(serviceRepository.findByParentService(parent)).thenReturn(new ArrayList<>());
        when(serviceRepository.save(any(Service.class))).thenAnswer(i -> i.getArgument(0));

        managerService.addSubService(1L, "Plumbing", "Fix pipes", 100L);

        verify(serviceRepository).save(any(Service.class));
    }

    @Test
    void addSubService_shouldThrowException_whenDuplicateName() {
        Service parent = new Service();
        Service existing = new Service();
        existing.setServiceName("Plumbing");

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(serviceRepository.findByParentService(parent)).thenReturn(List.of(existing));

        assertThrows(InvalidOperationException.class,
                () -> managerService.addSubService(1L, "Plumbing", "desc", 100L));
    }

    @Test
    void addSubService_shouldThrowException_whenParentNotFound() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> managerService.addSubService(99L, "Plumbing", "desc", 100L));
    }

    // =====================================================
    // UPDATE SERVICE
    // =====================================================

    @Test
    void updateService_shouldUpdateFields() {
        Service service = new Service();
        service.setServiceName("Old Name");

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        managerService.updateService(1L, "New Name", "New desc", 200L);

        assertEquals("New Name", service.getServiceName());
        assertEquals("New desc", service.getServiceDescription());
        assertEquals(200L, service.getServiceBasePrice());
    }

    @Test
    void updateService_shouldThrowException_whenNotFound() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> managerService.updateService(99L, "name", "desc", 100L));
    }

    // =====================================================
    // REMOVE SUB-SERVICE
    // =====================================================

    @Test
    void removeSubService_shouldDeleteService() {
        Service service = new Service();

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        managerService.removeSubService(1L);

        verify(serviceRepository).delete(service);
    }

    @Test
    void removeSubService_shouldThrowException_whenNotFound() {
        when(serviceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> managerService.removeSubService(99L));
    }

    // =====================================================
    // ADD SPECIALIST TO SERVICE
    // =====================================================

    @Test
    void addSpecialistToService_shouldAddService() {
        Specialist specialist = new Specialist();
        specialist.setServices(new ArrayList<>());

        Service service = new Service();

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        managerService.addSpecialistToService(1L, 1L);

        assertTrue(specialist.getServices().contains(service));
        verify(specialistRepository).save(specialist);
    }

    @Test
    void addSpecialistToService_shouldNotAddDuplicate() {
        Service service = new Service();

        Specialist specialist = new Specialist();
        List<Service> services = new ArrayList<>();
        services.add(service);
        specialist.setServices(services);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        managerService.addSpecialistToService(1L, 1L);

        // still only one service — no duplicate added
        assertEquals(1, specialist.getServices().size());
    }

    // =====================================================
    // REMOVE SPECIALIST FROM SERVICE
    // =====================================================

    @Test
    void removeSpecialistFromService_shouldRemoveService() {
        Service service = new Service();

        Specialist specialist = new Specialist();
        List<Service> services = new ArrayList<>();
        services.add(service);
        specialist.setServices(services);

        when(specialistRepository.findById(1L)).thenReturn(Optional.of(specialist));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        managerService.removeSpecialistFromService(1L, 1L);

        assertFalse(specialist.getServices().contains(service));
        verify(specialistRepository).save(specialist);
    }

    // =====================================================
    // GET ALL SPECIALISTS
    // =====================================================

    @Test
    void getAllSpecialists_shouldReturnAll() {
        List<Specialist> specialists = List.of(new Specialist(), new Specialist());
        when(specialistRepository.findAll()).thenReturn(specialists);

        List<Specialist> result = managerService.getAllSpecialists();

        assertEquals(2, result.size());
    }
}
