package com.example.canteenchecker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanteenDataDto {

  private final String id;
  private final String name;
  private final String dish;
  private final double  dishPrice;
  private final double  averageRating;

}
