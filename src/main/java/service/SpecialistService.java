package service;

import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;
import entity.Order;
import entity.Specialist;

import java.util.List;

public interface SpecialistService {

    SpecialistResponseDto signup(SpecialistSignupDto dto);

    Specialist login(String email, String password);

    Specialist findByEmail(String email);

    void updateProfile(Long specialistId, SpecialistSignupDto dto);

    List<Order> getAvailableOrders(Long specialistId);


    Long getWalletBalance(Long specialistId);

    void withdraw(Long specialistId, Long amount);
}
