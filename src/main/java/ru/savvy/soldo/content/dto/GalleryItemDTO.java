package ru.savvy.soldo.content.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItemDTO {
    private Long id;
    private String imageUrl;
    private String caption;
    private Integer sortOrder;
    private String createdAt;
}
