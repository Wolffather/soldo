package ru.savvy.soldo.config;

import ru.savvy.soldo.config.dto.AppConfigResponse;
import ru.savvy.soldo.config.dto.AppConfigUpdateRequest;

public interface AppConfigService {
    AppConfigResponse get();
    AppConfigResponse update(AppConfigUpdateRequest request);
}
