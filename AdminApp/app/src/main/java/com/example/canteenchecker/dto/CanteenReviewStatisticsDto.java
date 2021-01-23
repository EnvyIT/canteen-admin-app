package com.example.canteenchecker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanteenReviewStatisticsDto {

  private final int countOneStar;
  private final int countTwoStars;
  private final int countThreeStars;
  private final int countFourStars;
  private final int countFiveStars;

}
