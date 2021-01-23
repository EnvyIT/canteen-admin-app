package com.example.canteenchecker.infrastructure.mapper;

import com.example.canteenchecker.core.CanteenData;
import com.example.canteenchecker.dto.CanteenDataDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CanteenDataMapper {

  CanteenDataMapper Instance = Mappers.getMapper( CanteenDataMapper.class );

  CanteenDataDto map(CanteenData canteenData);

  CanteenData map(CanteenDataDto canteenDataDto);

}
