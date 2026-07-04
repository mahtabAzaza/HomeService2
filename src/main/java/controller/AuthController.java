package controller;

import DTO.CustomerResponseDto;
import DTO.CustomerSignupDto;
import DTO.SpecialistResponseDto;
import DTO.SpecialistSignupDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    // ثبت نام مشتری
    @PostMapping("/register/customer")
    public ResponseEntity<CustomerResponseDto> registerCustomer(
            @RequestBody CustomerSignupDto dto) {

        CustomerResponseDto response = authService.registerCustomer(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ثبت نام متخصص
    @PostMapping("/register/specialist")
    public ResponseEntity<SpecialistResponseDto> registerSpecialist(
            @RequestBody SpecialistSignupDto dto) {

        SpecialistResponseDto response = authService.registerSpecialist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
