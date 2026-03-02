package ru.savvy.soldo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.savvy.soldo.dto.ReviewDTO;
import ru.savvy.soldo.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewDTO entityToDto(Review review);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review dtoToEntity(ReviewDTO dto);
}
