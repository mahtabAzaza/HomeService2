package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.CustomerResponseDto;
import ir.HomeServiceApplication.DTO.CustomerSignupDto;
import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;

public interface AuthService {

    CustomerResponseDto registerCustomer(CustomerSignupDto dto);

    SpecialistResponseDto registerSpecialist(SpecialistSignupDto dto);
}
