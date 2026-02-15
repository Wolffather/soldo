package ru.savvy.soldo.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {
    KIDS(7, 11, "Дети"),
    TEENS(12, 15, "Подростки"),
    YOUNG_ADULTS(16, 20, "Молодёжь"),
    MIXED(7, 20, "Смешанная");

    private final int minAge;
    private final int maxAge;
    private final String displayName;
}