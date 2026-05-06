package ru.savvy.soldo.shared.settings;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_settings", schema = "soldo_schema")
@Getter
@Setter
@NoArgsConstructor
public class AppSetting {

    @Id
    @Column(name = "key", nullable = false, length = 255)
    private String key;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    public AppSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
