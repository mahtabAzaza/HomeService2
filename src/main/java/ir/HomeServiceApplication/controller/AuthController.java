package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.*;
import ir.HomeServiceApplication.entity.Customer;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.mapper.CustomerMapper;
import ir.HomeServiceApplication.mapper.SpecialistMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ir.HomeServiceApplication.service.AuthService;
import ir.HomeServiceApplication.service.CustomerService;
import ir.HomeServiceApplication.service.SpecialistService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final SpecialistService specialistService;

    public AuthController(AuthService authService,
                          CustomerService customerService,
                          SpecialistService specialistService) {
        this.authService = authService;
        this.customerService = customerService;
        this.specialistService = specialistService;
    }

    // ثبت نام مشتری
    @PostMapping("/register/customer")
    public ResponseEntity<CustomerResponseDto> registerCustomer(
            @Valid @RequestBody CustomerSignupDto dto) {

        CustomerResponseDto response = authService.registerCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ثبت نام متخصص
    @PostMapping("/register/specialist")
    public ResponseEntity<SpecialistResponseDto> registerSpecialist(
            @Valid @RequestBody SpecialistSignupDto dto) {

        SpecialistResponseDto response = authService.registerSpecialist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ورود مشتری
    @PostMapping("/login/customer")
    public ResponseEntity<CustomerResponseDto> loginCustomer(
            @Valid @RequestBody LogInDto dto) {

        Customer customer = customerService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(CustomerMapper.toDto(customer));
    }

    // ورود متخصص
    @PostMapping("/login/specialist")
    public ResponseEntity<SpecialistResponseDto> loginSpecialist(
            @Valid @RequestBody LogInDto dto) {

        Specialist specialist = specialistService.login(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(SpecialistMapper.toDto(specialist));
    }
}
