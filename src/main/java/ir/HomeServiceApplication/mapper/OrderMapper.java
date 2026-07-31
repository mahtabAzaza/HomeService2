package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.OrderDto;
import ir.HomeServiceApplication.DTO.UserSearchResponseDto;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.entity.User;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderDescription(order.getOrderDescription());
        dto.setPriceOffer(order.getPriceOffer());
        dto.setOrderStartDateTime(order.getOrderStartDateTime());
        dto.setAddress(order.getAddress());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setServiceId(order.getService().getId());
        return dto;
    }
    // -------to mapper
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
