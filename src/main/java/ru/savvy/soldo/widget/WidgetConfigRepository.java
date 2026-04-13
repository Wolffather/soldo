package ru.savvy.soldo.widget;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.savvy.soldo.widget.model.WidgetConfig;

public interface WidgetConfigRepository extends JpaRepository<WidgetConfig, Long> {
}
