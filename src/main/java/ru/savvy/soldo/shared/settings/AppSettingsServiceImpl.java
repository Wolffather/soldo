package ru.savvy.soldo.shared.settings;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppSettingsServiceImpl implements AppSettingsService {

    private final AppSettingRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getAll() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(AppSetting::getKey, s -> s.getValue() == null ? "" : s.getValue()));
    }

    @Override
    @Transactional
    public void saveAll(Map<String, String> settings) {
        settings.forEach((key, value) -> {
            AppSetting setting = repository.findById(key).orElse(new AppSetting(key, value));
            setting.setValue(value);
            repository.save(setting);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public String get(String key, String defaultValue) {
        return repository.findById(key)
                .map(AppSetting::getValue)
                .filter(v -> v != null && !v.isBlank())
                .orElse(defaultValue);
    }
}
