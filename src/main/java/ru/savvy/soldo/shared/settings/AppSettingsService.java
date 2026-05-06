package ru.savvy.soldo.shared.settings;

import java.util.Map;

public interface AppSettingsService {
    Map<String, String> getAll();
    void saveAll(Map<String, String> settings);
    String get(String key, String defaultValue);
}
