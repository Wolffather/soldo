package ru.savvy.soldo.onboarding;

import ru.savvy.soldo.onboarding.dto.RegisterRequest;
import ru.savvy.soldo.onboarding.dto.RegisterResponse;

public interface OnboardingService {
    RegisterResponse register(RegisterRequest request);
}
