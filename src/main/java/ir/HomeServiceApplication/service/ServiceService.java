package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.ServiceResponseDto;
import ir.HomeServiceApplication.entity.Service;
import ir.HomeServiceApplication.exception.NotFoundException;
import ir.HomeServiceApplication.mapper.ServiceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ir.HomeServiceApplication.repository.ServiceRepository;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional(readOnly = true)
    public Page<ServiceResponseDto> getAllParentServices(Pageable pageable) {

        return serviceRepository.findByParentServiceIsNull(pageable)
                .map(serviceMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Service not found"));
    }
}
