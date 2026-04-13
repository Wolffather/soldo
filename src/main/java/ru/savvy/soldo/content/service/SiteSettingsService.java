package ru.savvy.soldo.content.service;

import java.util.Map;

public interface SiteSettingsService {
    Map<String, String> getAll();
    void upsert(String key, String value);
    void delete(String key);
}
