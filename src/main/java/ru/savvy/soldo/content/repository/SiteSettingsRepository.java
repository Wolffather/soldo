package ru.savvy.soldo.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.content.model.SiteSettings;

import java.util.Optional;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
    Optional<SiteSettings> findBySettingKey(String key);
}
