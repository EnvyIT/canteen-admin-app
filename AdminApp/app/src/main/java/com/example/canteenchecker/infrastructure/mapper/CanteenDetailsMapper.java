package com.example.canteenchecker.infrastructure.mapper;

import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.dto.CanteenDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CanteenDetailsMapper {

  CanteenDetailsMapper Instance = Mappers.getMapper( CanteenDetailsMapper.class );

  CanteenDetailsDto map(CanteenDetails canteenDetails);

  CanteenDetails map(CanteenDetailsDto canteenDetailsDto);

}
