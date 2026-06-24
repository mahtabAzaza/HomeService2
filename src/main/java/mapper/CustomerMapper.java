package mapper;

import DTO.CustomerSignupDto;
import DTO.CustomerDto;
import entity.Customer;

import java.time.LocalDateTime;

public class CustomerMapper {

    // CreateDTO -> Entity
    public static Customer toEntity(CustomerSignupDto dto) {

        Customer customer = new Customer();

        customer.setName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(dto.getPassword());

        // مقداردهی سیستمی
        customer.setCustomerRegisterDate(LocalDateTime.now());

        return customer;
    }


    // Entity -> DTO
    public static CustomerDto toDTO(Customer customer) {

        CustomerDto dto = new CustomerDto();
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());

        return dto;
    }

}