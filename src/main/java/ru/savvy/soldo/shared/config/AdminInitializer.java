package ru.savvy.soldo.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.savvy.soldo.tenant.model.TenantStatus;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.tenant.TenantRepository;
import ru.savvy.soldo.tenant.model.Tenant;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-password:admin}")
    private String defaultAdminPassword;

    @Override
    public void run(String... args) {

        if (tenantRepository.findById(1L).isEmpty()) {
            Tenant tenant = Tenant.builder()
                    .name("Default")
                    .slug("default")
                    .status(TenantStatus.TRIAL)
                    .build();
            tenantRepository.save(tenant);
        }



        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .passwordHash(passwordEncoder.encode(defaultAdminPassword))
                    .role("ADMIN")
                    .tenantId(1L)
                    .build();
            userRepository.save(admin);
        }
    }
}