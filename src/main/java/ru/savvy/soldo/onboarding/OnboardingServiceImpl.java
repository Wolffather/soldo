package ru.savvy.soldo.onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.onboarding.dto.BusinessType;
import ru.savvy.soldo.onboarding.dto.RegisterRequest;
import ru.savvy.soldo.onboarding.dto.RegisterResponse;
import ru.savvy.soldo.onboarding.dto.SlugCheckResponse;
import ru.savvy.soldo.shared.exception.DataDuplicationException;
import ru.savvy.soldo.shared.security.JwtTokenProvider;
import ru.savvy.soldo.tenant.TenantConfigRepository;
import ru.savvy.soldo.tenant.TenantRepository;
import ru.savvy.soldo.tenant.TenantSubscriptionRepository;
import ru.savvy.soldo.tenant.model.PlanType;
import ru.savvy.soldo.tenant.model.Tenant;
import ru.savvy.soldo.tenant.model.TenantConfig;
import ru.savvy.soldo.tenant.model.TenantStatus;
import ru.savvy.soldo.tenant.model.TenantSubscription;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.repository.UserRepository;
import ru.savvy.soldo.widget.WidgetConfigRepository;
import ru.savvy.soldo.widget.model.WidgetConfig;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService {

    private final TenantRepository tenantRepository;
    private final TenantConfigRepository tenantConfigRepository;
    private final TenantSubscriptionRepository tenantSubscriptionRepository;
    private final WidgetConfigRepository widgetConfigRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (tenantRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new DataDuplicationException("Slug уже занят");
        }

        // 1. Create Tenant
        Tenant tenant;
        try {
            tenant = tenantRepository.save(
                    Tenant.builder()
                            .slug(request.getSlug())
                            .name(request.getOrgName())
                            .status(TenantStatus.TRIAL)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw new DataDuplicationException("Slug уже занят");
        }

        // 2. Create TenantConfig with terminology from businessType
        String[] terminology = getTerminology(request.getBusinessType());
        TenantConfig tenantConfig = TenantConfig.builder()
                .eventLabel(terminology[0])
                .participantLabel(terminology[1])
                .bookingLabel(terminology[2])
                .build();
        tenantConfig.setTenant(tenant);
        tenantConfigRepository.save(tenantConfig);

        // 3. Create TenantSubscription
        TenantSubscription subscription = TenantSubscription.builder()
                .tenant(tenant)
                .plan(PlanType.FREE)
                .maxEvents(10)
                .maxBookingsPerMonth(50)
                .maxAdminUsers(1)
                .customDomainEnabled(false)
                .apiAccessEnabled(false)
                .build();
        tenantSubscriptionRepository.save(subscription);

        // 4. Create WidgetConfig with defaults
        WidgetConfig widgetConfig = WidgetConfig.builder().build();
        widgetConfig.setTenant(tenant);
        widgetConfigRepository.save(widgetConfig);

        // 5. Create admin User
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
                .tenantId(tenant.getId())
                .build();
        user = userRepository.save(user);

        // 6. Generate JWT
        String token = jwtTokenProvider.generateToken(
                String.valueOf(user.getId()), "ADMIN", tenant.getId()
        );

        return new RegisterResponse(token, tenant.getId(), tenant.getSlug(), tenant.getName());
    }

    @Override
    public SlugCheckResponse checkSlug(String slug) {
        if (tenantRepository.findBySlug(slug).isEmpty()) {
            return new SlugCheckResponse(true, null);
        }
        // Find a free suggestion
        int suffix = 2;
        String candidate;
        do {
            candidate = slug + "-" + suffix;
            suffix++;
        } while (tenantRepository.findBySlug(candidate).isPresent());
        return new SlugCheckResponse(false, candidate);
    }

    @Override
    public String generateSlug(String orgName) {
        String transliterated = transliterate(orgName);
        return transliterated
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .trim();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String[] getTerminology(BusinessType type) {
        // returns [eventLabel, participantLabel, bookingLabel]
        return switch (type) {
            case CAMP   -> new String[]{"Смена",   "Участник", "Запись"};
            case STUDIO -> new String[]{"Занятие", "Клиент",   "Запись"};
            case SCHOOL -> new String[]{"Занятие", "Студент",  "Запись"};
            case CLINIC -> new String[]{"Приём",   "Пациент",  "Запись"};
            case TOUR   -> new String[]{"Тур",     "Гость",    "Бронирование"};
            case OTHER  -> new String[]{"Событие", "Участник", "Бронирование"};
        };
    }

    private String transliterate(String input) {
        StringBuilder sb = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String replacement = switch (c) {
                case 'а', 'А' -> "a";
                case 'б', 'Б' -> "b";
                case 'в', 'В' -> "v";
                case 'г', 'Г' -> "g";
                case 'д', 'Д' -> "d";
                case 'е', 'Е' -> "e";
                case 'ё', 'Ё' -> "yo";
                case 'ж', 'Ж' -> "zh";
                case 'з', 'З' -> "z";
                case 'и', 'И' -> "i";
                case 'й', 'Й' -> "j";
                case 'к', 'К' -> "k";
                case 'л', 'Л' -> "l";
                case 'м', 'М' -> "m";
                case 'н', 'Н' -> "n";
                case 'о', 'О' -> "o";
                case 'п', 'П' -> "p";
                case 'р', 'Р' -> "r";
                case 'с', 'С' -> "s";
                case 'т', 'Т' -> "t";
                case 'у', 'У' -> "u";
                case 'ф', 'Ф' -> "f";
                case 'х', 'Х' -> "kh";
                case 'ц', 'Ц' -> "ts";
                case 'ч', 'Ч' -> "ch";
                case 'ш', 'Ш' -> "sh";
                case 'щ', 'Щ' -> "shch";
                case 'ъ', 'Ъ' -> "";
                case 'ы', 'Ы' -> "y";
                case 'ь', 'Ь' -> "";
                case 'э', 'Э' -> "e";
                case 'ю', 'Ю' -> "yu";
                case 'я', 'Я' -> "ya";
                default        -> String.valueOf(c);
            };
            sb.append(replacement);
        }
        return sb.toString();
    }
}
