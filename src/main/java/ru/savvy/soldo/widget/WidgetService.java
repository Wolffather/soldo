package ru.savvy.soldo.widget;

import ru.savvy.soldo.widget.dto.WidgetBookingRequest;
import ru.savvy.soldo.widget.dto.WidgetBookingResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigUpdateRequest;
import ru.savvy.soldo.widget.dto.WidgetEventResponse;

import java.util.List;

public interface WidgetService {

    WidgetConfigResponse getConfig();

    WidgetConfigResponse updateConfig(WidgetConfigUpdateRequest req);

    List<WidgetEventResponse> getEvents(Long categoryId);

    WidgetBookingResponse createBooking(WidgetBookingRequest req);
}
