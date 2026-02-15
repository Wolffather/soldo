package ru.savvy.soldo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.savvy.soldo.model.User;
import ru.savvy.soldo.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .telegramId(1111L)
                    .passwordHash(passwordEncoder.encode("admin"))
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
        }
    }
}