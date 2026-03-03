package ru.savvy.soldo.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.review.dto.ReviewDTO;
import ru.savvy.soldo.review.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO entityToDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review dtoToEntity(ReviewDTO dto);
}
