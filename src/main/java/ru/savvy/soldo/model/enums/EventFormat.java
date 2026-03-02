package ru.savvy.soldo.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EventFormat {
    SESSION_CITY("Смена городская"),
    SESSION_OUTDOOR("Смена загородная"),
    RECURRING("Повторяющееся"),
    ONE_TIME("Единовременное"),
    TRIP("Выезд");

    private final String label;

    public static final Set<EventFormat> SESSION_FORMATS = Set.of(SESSION_CITY, SESSION_OUTDOOR);
}