package ir.HomeServiceApplication.controller;

import ir.HomeServiceApplication.DTO.*;
import ir.HomeServiceApplication.entity.Customer;
import ir.HomeServiceApplication.entity.Manager;
import ir.HomeServiceApplication.entity.Specialist;
import ir.HomeServiceApplication.mapper.CustomerMapper;
import ir.HomeServiceApplication.mapper.ManagerMapper;
import ir.HomeServiceApplication.mapper.SpecialistMapper;
import ir.HomeServiceApplication.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ir.HomeServiceApplication.service.AuthService;
import ir.HomeServiceApplication.service.CustomerService;
import ir.HomeServiceApplication.service.SpecialistService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CustomerService customerService;
    private final SpecialistService specialistService;
    private final ManagerService managerService;
    private final ir.HomeServiceApplication.security.JwtUtil jwtUtil;

    public AuthController(AuthService authService,
                          CustomerService customerService,
                          SpecialistService specialistService,
                          ManagerService managerService,
                          ir.HomeServiceApplication.security.JwtUtil jwtUtil) {
        this.authService = authService;
        this.customerService = customerService;
        this.specialistService = specialistService;
        this.managerService = managerService;
        this.jwtUtil = jwtUtil;
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

    @PostMapping("/login/customer")
    public ResponseEntity<LoginResponseDto> loginCustomer(
            @Valid @RequestBody LogInDto dto) {

        Customer customer = customerService.login(dto.getEmail(), dto.getPassword());
        String token = jwtUtil.generateToken(customer.getEmail(), "CUSTOMER");
        return ResponseEntity.ok(new LoginResponseDto(token, "CUSTOMER", CustomerMapper.toDto(customer)));
    }

    @PostMapping("/login/specialist")
    public ResponseEntity<LoginResponseDto> loginSpecialist(
            @Valid @RequestBody LogInDto dto) {

        Specialist specialist = specialistService.login(dto.getEmail(), dto.getPassword());
        String token = jwtUtil.generateToken(specialist.getEmail(), "SPECIALIST");
        return ResponseEntity.ok(new LoginResponseDto(token, "SPECIALIST", SpecialistMapper.toDto(specialist)));
    }

    @PostMapping("/login/manager")
    public ResponseEntity<LoginResponseDto> loginManager(
            @Valid @RequestBody LogInDto dto) {

        Manager manager = managerService.login(dto.getEmail(), dto.getPassword());
        String token = jwtUtil.generateToken(manager.getEmail(), "MANAGER");
        return ResponseEntity.ok(new LoginResponseDto(token, "MANAGER", ManagerMapper.toDto(manager)));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }
}
