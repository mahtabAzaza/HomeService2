package mapper;

import DTO.SpecialistResponseDto;
import entity.Specialist;

public class SpecialistMapper {

    public static SpecialistResponseDto toDto(Specialist specialist) {
        SpecialistResponseDto dto = new SpecialistResponseDto();
        dto.setId(specialist.getId());
        dto.setFirstName(specialist.getFirstName());
        dto.setLastName(specialist.getLastName());
        dto.setEmail(specialist.getEmail());
        dto.setStatus(specialist.getStatus().name());
        return dto;
    }
}
