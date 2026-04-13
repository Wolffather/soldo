package ru.savvy.soldo.onboarding;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.savvy.soldo.onboarding.dto.RegisterRequest;
import ru.savvy.soldo.onboarding.dto.RegisterResponse;
import ru.savvy.soldo.onboarding.dto.SlugCheckResponse;

import java.util.Map;

@RestController
@RequestMapping("/public/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding")
public class OnboardingController {

    private final OnboardingService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @GetMapping("/check-slug")
    public SlugCheckResponse checkSlug(@RequestParam String slug) {
        return service.checkSlug(slug);
    }

    @GetMapping("/generate-slug")
    public Map<String, String> generateSlug(@RequestParam String name) {
        return Map.of("slug", service.generateSlug(name));
    }
}
