package ir.HomeServiceApplication.service;

import ir.HomeServiceApplication.DTO.SpecialistResponseDto;
import ir.HomeServiceApplication.DTO.SpecialistSignupDto;
import ir.HomeServiceApplication.entity.Order;
import ir.HomeServiceApplication.entity.Specialist;

import java.util.List;

public interface SpecialistService {

    SpecialistResponseDto signup(SpecialistSignupDto dto);

    Specialist login(String email, String password);

    Specialist findByEmail(String email);

    void updateProfile(Long specialistId, SpecialistSignupDto dto);

    List<Order> getAvailableOrders(Long specialistId);


    Long getWalletBalance(Long specialistId);

    void withdraw(Long specialistId, Long amount);
    void score(Long specialistId);
}
