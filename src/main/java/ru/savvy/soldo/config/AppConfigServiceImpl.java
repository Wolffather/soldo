package ru.savvy.soldo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.savvy.soldo.config.dto.AppConfigResponse;
import ru.savvy.soldo.config.dto.AppConfigUpdateRequest;

@Service
@RequiredArgsConstructor
public class AppConfigServiceImpl implements AppConfigService {

    private static final Long CONFIG_ID = 1L;

    private final AppConfigRepository repository;

    @Override
    public AppConfigResponse get() {
        return toResponse(loadOrCreate());
    }

    @Override
    @Transactional
    public AppConfigResponse update(AppConfigUpdateRequest req) {
        AppConfig config = loadOrCreate();
        if (req.getName() != null && !req.getName().isBlank()) config.setName(req.getName());
        if (req.getDomain() != null) config.setDomain(req.getDomain().isBlank() ? null : req.getDomain());
        if (req.getEventLabel() != null && !req.getEventLabel().isBlank()) config.setEventLabel(req.getEventLabel());
        if (req.getParticipantLabel() != null && !req.getParticipantLabel().isBlank()) config.setParticipantLabel(req.getParticipantLabel());
        if (req.getBookingLabel() != null && !req.getBookingLabel().isBlank()) config.setBookingLabel(req.getBookingLabel());
        return toResponse(repository.save(config));
    }

    private AppConfig loadOrCreate() {
        return repository.findById(CONFIG_ID).orElseGet(() ->
                repository.save(AppConfig.builder()
                        .id(CONFIG_ID)
                        .name("Моя организация")
                        .build()));
    }

    private AppConfigResponse toResponse(AppConfig c) {
        return AppConfigResponse.builder()
                .name(c.getName())
                .domain(c.getDomain())
                .eventLabel(c.getEventLabel())
                .participantLabel(c.getParticipantLabel())
                .bookingLabel(c.getBookingLabel())
                .build();
    }
}
