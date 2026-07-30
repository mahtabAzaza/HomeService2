package ir.HomeServiceApplication.config;

import ir.HomeServiceApplication.entity.Manager;
import ir.HomeServiceApplication.entity.Role;
import ir.HomeServiceApplication.repository.ManagerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(ManagerRepository managerRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (managerRepository.findByEmail("admin@homeservice.com") == null) {
            Manager manager = new Manager();
            manager.setFirstName("Admin");
            manager.setLastName("Admin");
            manager.setEmail("admin@homeservice.com");
            manager.setPassword(passwordEncoder.encode("Admin1234"));
            manager.setRole(Role.MANAGER);
            managerRepository.save(manager);
        }
    }
}