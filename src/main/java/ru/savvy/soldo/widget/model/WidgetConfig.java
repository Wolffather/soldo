package ru.savvy.soldo.widget.model;

import jakarta.persistence.*;
import lombok.*;
import ru.savvy.soldo.tenant.model.Tenant;

import java.time.LocalDateTime;

@Entity
@Table(name = "widget_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetConfig {

    @Id
    @Column(name = "tenant_id")
    private Long tenantId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(name = "primary_color")
    @Builder.Default
    private String primaryColor = "#2563eb";

    @Column(name = "background_color")
    @Builder.Default
    private String backgroundColor = "#ffffff";

    @Column(name = "text_color")
    @Builder.Default
    private String textColor = "#111827";

    @Column(name = "button_text_color")
    @Builder.Default
    private String buttonTextColor = "#ffffff";

    @Column(name = "border_radius")
    @Builder.Default
    private String borderRadius = "8px";

    @Column(name = "font_family")
    @Builder.Default
    private String fontFamily = "inherit";

    @Column(name = "show_category_step")
    @Builder.Default
    private Boolean showCategoryStep = true;

    @Column(name = "success_message")
    @Builder.Default
    private String successMessage = "Бронирование успешно создано! Мы свяжемся с вами.";

    @Column(name = "custom_css")
    private String customCss;

    @Column(name = "category_step_title")
    @Builder.Default
    private String categoryStepTitle = "Выберите направление";

    @Column(name = "button_label")
    @Builder.Default
    private String buttonLabel = "Записаться";

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
