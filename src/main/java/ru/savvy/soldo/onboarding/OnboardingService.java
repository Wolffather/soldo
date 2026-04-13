package ru.savvy.soldo.onboarding;

import ru.savvy.soldo.onboarding.dto.RegisterRequest;
import ru.savvy.soldo.onboarding.dto.RegisterResponse;
import ru.savvy.soldo.onboarding.dto.SlugCheckResponse;

public interface OnboardingService {
    RegisterResponse register(RegisterRequest request);
    SlugCheckResponse checkSlug(String slug);
    String generateSlug(String orgName);
}
