package com.example.canteenchecker.infrastructure.mapper;

import com.example.canteenchecker.core.ReviewData;
import com.example.canteenchecker.dto.ReviewDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReviewDataMapper {

  ReviewDataMapper Instance = Mappers.getMapper( ReviewDataMapper.class );

  ReviewDataDto map(ReviewData reviewData);

  ReviewData map(ReviewDataDto reviewDataDto);

}
