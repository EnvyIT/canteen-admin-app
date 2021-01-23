package com.example.canteenchecker.infrastructure.mapper;

import com.example.canteenchecker.core.CanteenReviewStatistics;
import com.example.canteenchecker.dto.CanteenReviewStatisticsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CanteenReviewStatisticsMapper {

  CanteenReviewStatisticsMapper Instance = Mappers.getMapper( CanteenReviewStatisticsMapper.class );

  CanteenReviewStatisticsDto map(CanteenReviewStatistics canteenReviewStatistics);

  CanteenReviewStatistics map(CanteenReviewStatisticsDto canteenReviewStatisticsDto);

}
