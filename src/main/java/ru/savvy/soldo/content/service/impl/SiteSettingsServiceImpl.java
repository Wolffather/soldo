package ru.savvy.soldo.content.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.savvy.soldo.content.model.SiteSettings;
import ru.savvy.soldo.content.repository.SiteSettingsRepository;
import ru.savvy.soldo.content.service.SiteSettingsService;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteSettingsServiceImpl implements SiteSettingsService {

    private final SiteSettingsRepository repository;

    @Override
    public Map<String, String> getAll() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(SiteSettings::getSettingKey, s ->
                        s.getSettingValue() == null ? "" : s.getSettingValue()));
    }

    @Override
    public void upsert(String key, String value) {
        SiteSettings setting = repository.findBySettingKey(key)
                .orElseGet(() -> SiteSettings.builder().settingKey(key).build());
        setting.setSettingValue(value);
        repository.save(setting);
    }

    @Override
    public void delete(String key) {
        repository.findBySettingKey(key).ifPresent(repository::delete);
    }
}
