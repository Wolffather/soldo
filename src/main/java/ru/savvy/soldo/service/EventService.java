package ru.savvy.soldo.service;

import ru.savvy.soldo.model.Event;

import java.util.List;

public interface EventService {


    List<Event> getAllEvents();

    Event saveEvent(Event event);
}
