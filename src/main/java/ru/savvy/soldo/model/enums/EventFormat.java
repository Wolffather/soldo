package ru.savvy.soldo.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventFormat {
    CAMP_CITY("Городской лагерь"),
    CAMP_OUTDOOR("Загородный лагерь"),
    RECURRING("Повторяющееся"),
    ONE_TIME("Единовременное"),
    TRIP("Выезд");

    private final String label;
}