package service;

import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;

public interface AuthService {

    CustomerResponseDto registerCustomer(CustomerSignupDto dto);

    SpecialistResponseDto registerSpecialist(SpecialistSignupDto dto);
}
