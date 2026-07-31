package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.OrderDto;
import ir.HomeServiceApplication.DTO.OrderHistoryDetailDto;
import ir.HomeServiceApplication.DTO.OrderHistorySummaryDto;
import ir.HomeServiceApplication.DTO.UserSearchResponseDto;
import ir.HomeServiceApplication.entity.Customer;
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

    // خلاصه برای لیست تاریخچه — جزئیات کامل فقط با درخواست جداگانه روی هر سفارش
    public static OrderHistorySummaryDto toHistorySummaryDto(Order order) {
        OrderHistorySummaryDto dto = new OrderHistorySummaryDto();
        dto.setId(order.getId());
        dto.setServiceName(order.getService() != null ? order.getService().getServiceName() : null);
        dto.setCustomerName(fullName(order.getCustomer()));
        dto.setSpecialistName(fullName(order.getSpecialist()));
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setOrderDate(order.getOrderSubmitDateTime());
        return dto;
    }

    public static OrderHistoryDetailDto toHistoryDetailDto(Order order) {
        OrderHistoryDetailDto dto = new OrderHistoryDetailDto();
        dto.setId(order.getId());
        dto.setOrderDescription(order.getOrderDescription());
        dto.setAddress(order.getAddress());
        dto.setOrderStatus(order.getOrderStatus().name());
        dto.setPriceOffer(order.getPriceOffer());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setOrderStartDateTime(order.getOrderStartDateTime());
        dto.setOrderSubmitDateTime(order.getOrderSubmitDateTime());
        dto.setCompletionTime(order.getCompletionTime());

        if (order.getService() != null) {
            dto.setServiceId(order.getService().getId());
            dto.setServiceName(order.getService().getServiceName());
        }

        Customer customer = order.getCustomer();
        if (customer != null) {
            dto.setCustomerId(customer.getId());
            dto.setCustomerName(fullName(customer));
            dto.setCustomerEmail(customer.getEmail());
        }

        Specialist specialist = order.getSpecialist();
        if (specialist != null) {
            dto.setSpecialistId(specialist.getId());
            dto.setSpecialistName(fullName(specialist));
            dto.setSpecialistEmail(specialist.getEmail());
        }

        return dto;
    }

    private static String fullName(User user) {
        if (user == null) {
            return null;
        }
        return user.getFirstName() + " " + user.getLastName();
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
