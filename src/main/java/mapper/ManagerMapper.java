package mapper;

import entity.Manager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagerMapper {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ManagerResponseDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    public static ManagerResponseDto toDto(Manager manager) {
        ManagerResponseDto dto = new ManagerResponseDto();
        dto.setId(manager.getId());
        dto.setFirstName(manager.getFirstName());
        dto.setLastName(manager.getLastName());
        dto.setEmail(manager.getEmail());
        return dto;
    }
}
