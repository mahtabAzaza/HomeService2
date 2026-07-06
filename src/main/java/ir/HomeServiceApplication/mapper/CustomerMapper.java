package ir.HomeServiceApplication.mapper;

import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.entity.Customer;

public class CustomerMapper {

    public static CustomerResponseDto toDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        return dto;
    }
}
