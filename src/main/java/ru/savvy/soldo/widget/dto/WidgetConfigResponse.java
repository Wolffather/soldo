package ru.savvy.soldo.widget.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WidgetConfigResponse {

    private String primaryColor;
    private String backgroundColor;
    private String textColor;
    private String buttonTextColor;
    private String borderRadius;
    private String fontFamily;
    private Boolean showCategoryStep;
    private String successMessage;
    private String customCss;
    private String categoryStepTitle;
    private String buttonLabel;
    private String tenantSlug;
    private String tenantName;
}
