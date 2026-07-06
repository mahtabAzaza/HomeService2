package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.OrderDto;
import ir.HomeServiceApplication.entity.Order;

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
}
