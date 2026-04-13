package ru.savvy.soldo.event.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum EventFormat {
    RECURRING("Повторяющееся"),
    ONE_TIME("Единовременное");

    private final String label;
}