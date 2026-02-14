package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.dto.EventBookingSummaryResponse;
import ru.savvy.soldo.model.EventBookingsSummary;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventBookingSummaryMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    EventBookingSummaryResponse entityToResponse(EventBookingsSummary summary);

    List<EventBookingSummaryResponse> entitiesToResponses(
            List<EventBookingsSummary> summaries);
}