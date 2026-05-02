package ru.savvy.soldo.onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.config.AppConfig;
import ru.savvy.soldo.config.AppConfigRepository;
import ru.savvy.soldo.onboarding.dto.BusinessType;
import ru.savvy.soldo.onboarding.dto.RegisterRequest;
import ru.savvy.soldo.onboarding.dto.RegisterResponse;
import ru.savvy.soldo.shared.security.JwtTokenProvider;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.widget.WidgetConfigRepository;
import ru.savvy.soldo.widget.model.WidgetConfig;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private static final Long SINGLETON_ID = 1L;

    private final AppConfigRepository appConfigRepository;
    private final WidgetConfigRepository widgetConfigRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. Initialize AppConfig (org name + terminology)
        String[] terminology = getTerminology(request.getBusinessType());
        AppConfig appConfig = appConfigRepository.findById(SINGLETON_ID)
                .orElse(AppConfig.builder().id(SINGLETON_ID).build());
        appConfig.setName(request.getOrgName());
        appConfig.setEventLabel(terminology[0]);
        appConfig.setParticipantLabel(terminology[1]);
        appConfig.setBookingLabel(terminology[2]);
        appConfigRepository.save(appConfig);

        // 2. Initialize WidgetConfig
        if (widgetConfigRepository.findById(SINGLETON_ID).isEmpty()) {
            widgetConfigRepository.save(WidgetConfig.builder().id(SINGLETON_ID).build());
        }

        // 3. Create admin User
        String firstName;
        String lastName;
        int spaceIdx = request.getAdminName().indexOf(' ');
        if (spaceIdx >= 0) {
            firstName = request.getAdminName().substring(0, spaceIdx);
            lastName = request.getAdminName().substring(spaceIdx + 1);
        } else {
            firstName = request.getAdminName();
            lastName = "";
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .username(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                .build();
        user = userRepository.save(user);

        // 4. Generate JWT
        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), "ADMIN");

        return new RegisterResponse(token);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String[] getTerminology(BusinessType type) {
        return switch (type) {
            case CAMP   -> new String[]{"Смена",   "Участник", "Запись"};
            case STUDIO -> new String[]{"Занятие", "Клиент",   "Запись"};
            case SCHOOL -> new String[]{"Занятие", "Студент",  "Запись"};
            case TOUR   -> new String[]{"Тур",     "Гость",    "Бронирование"};
            case OTHER  -> new String[]{"Событие", "Участник", "Бронирование"};
        };
    }
}
