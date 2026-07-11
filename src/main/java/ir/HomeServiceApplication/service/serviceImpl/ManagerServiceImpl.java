package ir.HomeServiceApplication.service.serviceImpl;

import ir.HomeServiceApplication.DTO.UserFilterDto;
import ir.HomeServiceApplication.DTO.UserSearchResponseDto;
import ir.HomeServiceApplication.entity.Service;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.SpecialistStatus;
import ir.HomeServiceApplication.entity.User;
import ir.HomeServiceApplication.specification.UserSpecification;

//import org.springframework.stereotype.Service;
import ir.HomeServiceApplication.exception.InvalidOperationException;
import ir.HomeServiceApplication.exception.NotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.ServiceRepository;
import ir.HomeServiceApplication.repository.SpecialistRepository;
import ir.HomeServiceApplication.repository.UserRepository;
import ir.HomeServiceApplication.service.ManagerService;
import java.util.List;
import java.util.stream.Collectors;

// ?????
@org.springframework.stereotype.Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final SpecialistRepository specialistRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ManagerServiceImpl(SpecialistRepository specialistRepository,
                              ServiceRepository serviceRepository,
                              UserRepository userRepository) {
        this.specialistRepository = specialistRepository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    // تایید متخصص
    @Override
    public void approveSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        specialist.setStatus(SpecialistStatus.APPROVED);
    }

    //حذف متخصص
    @Override
    public void deleteSpecialist(Long specialistId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        specialistRepository.delete(specialist);
    }

    // ایجاد سرویس
//    @Override
//    public void createService(String name, String description, Long basePrice) {
//
//        Service service = new Service();
//        service.setServiceName(name);
//        service.setServiceDescription(description);
//        service.setServiceBasePrice(basePrice);
//        service.setParentService(null);
//
//        serviceRepository.save(service);
//    }

    @Override
    public Service createService(String name, String description, Long basePrice) {

        Service service = new Service();
        service.setServiceName(name);
        service.setServiceDescription(description);
        service.setServiceBasePrice(basePrice);
        service.setParentService(null);

        return serviceRepository.save(service);
    }

//    ایجاد زیر سرویس
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


    // ویرایش سرویس
    @Override
    public void updateService(Long serviceId, String name, String description, Long basePrice) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        service.setServiceName(name);
        service.setServiceDescription(description);
        service.setServiceBasePrice(basePrice);
    }


    // حذف زیرسرویس
    @Override
    public void removeSubService(Long serviceId) {

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        serviceRepository.delete(service);
    }


    // اضافه کرپن متخصص به زیر سرویس
    @Override
    public void addSpecialistToService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        if (!specialist.getServices().contains(service)) {
            specialist.getServices().add(service);

        }
        specialistRepository.save(specialist);
    }


    // حذف کردن متخصص از زیر سرویس

    @Override
    public void removeSpecialistFromService(Long specialistId, Long serviceId) {

        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new NotFoundException("Specialist not found"));

        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        specialist.getServices().remove(service);
        specialistRepository.save(specialist);
        // save??
    }

    // مشاهده متخصصان R
    @Override
    @Transactional(readOnly = true)
    public List<Specialist> getAllSpecialists() {
        return specialistRepository.findAll();
    }

    // جستجو و فیلتر کاربران
    @Override
    @Transactional(readOnly = true)
    public List<UserSearchResponseDto> searchUsers(UserFilterDto filter) {
        Specification<User> spec = Specification
                .where(UserSpecification.hasRole(filter.getRole()))
                .and(UserSpecification.firstNameContains(filter.getFirstName()))
                .and(UserSpecification.lastNameContains(filter.getLastName()))
                .and(UserSpecification.hasServiceNamed(filter.getServiceName()))
                .and(UserSpecification.scoreAtLeast(filter.getMinScore()))
                .and(UserSpecification.scoreAtMost(filter.getMaxScore()));

        return userRepository.findAll(spec).stream()
                .map(this::toSearchResponseDto)
                .collect(Collectors.toList());
    }

    private UserSearchResponseDto toSearchResponseDto(User user) {
        UserSearchResponseDto dto = new UserSearchResponseDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());

        if (user instanceof Specialist specialist) {
            dto.setStatus(specialist.getStatus() != null ? specialist.getStatus().name() : null);
            dto.setScore(specialist.getScore());
            if (specialist.getServices() != null) {
                dto.setServices(specialist.getServices().stream()
                        .map(s -> s.getServiceName())
                        .collect(Collectors.toList()));
            }
        }
        return dto;
    }
}
