package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.ServiceDto;
import ir.HomeServiceApplication.DTO.ServiceResponseDto;
import ir.HomeServiceApplication.entity.Service;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public static ServiceResponseDto toResponseDto(Service service) {
        if (service == null) {
            return null;
        }

        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setId(service.getId());
        dto.setServiceName(service.getServiceName());
        dto.setServiceBasePrice(service.getServiceBasePrice());
        dto.setServiceDescription(service.getServiceDescription());

        if (service.getParentService() != null) {
            dto.setParentServiceId(service.getParentService().getId());
        }

        return dto;
    }

    public Service toEntity(ServiceDto dto) {
        if (dto == null) {
            return null;
        }

        Service service = new Service();
        service.setServiceName(dto.getServiceName());
        service.setServiceBasePrice(dto.getServiceBasePrice());
        service.setServiceDescription(dto.getServiceDescription());

        return service;
    }
}