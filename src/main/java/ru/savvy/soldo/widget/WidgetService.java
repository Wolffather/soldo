package ru.savvy.soldo.widget;

import ru.savvy.soldo.widget.dto.WidgetBookingRequest;
import ru.savvy.soldo.widget.dto.WidgetBookingResponse;
import ru.savvy.soldo.widget.dto.WidgetCategoryResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigResponse;
import ru.savvy.soldo.widget.dto.WidgetConfigUpdateRequest;
import ru.savvy.soldo.widget.dto.WidgetEventResponse;

import java.util.List;

public interface WidgetService {

    WidgetConfigResponse getConfig(String tenantSlug);

    WidgetConfigResponse getConfigByUserId(Long userId);

    WidgetConfigResponse updateConfig(Long userId, WidgetConfigUpdateRequest req);

    List<WidgetCategoryResponse> getCategories(String tenantSlug);

    List<WidgetEventResponse> getEvents(String tenantSlug, Long categoryId);

    WidgetBookingResponse createBooking(WidgetBookingRequest req);
}
