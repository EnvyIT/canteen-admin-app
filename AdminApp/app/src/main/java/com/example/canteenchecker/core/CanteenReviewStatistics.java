package com.example.canteenchecker.core;

import java.io.Serializable;
import lombok.Data;

@Data
public class CanteenReviewStatistics implements Serializable {

  private final int countOneStar;
  private final int countTwoStars;
  private final int countThreeStars;
  private final int countFourStars;
  private final int countFiveStars;
  private final int totalRating;
  private final float averageRating;

  public CanteenReviewStatistics(int countOneStar, int countTwoStars, int countThreeStars, int countFourStars, int countFiveStars) {
    this.countOneStar = countOneStar;
    this.countTwoStars = countTwoStars;
    this.countThreeStars = countThreeStars;
    this.countFourStars = countFourStars;
    this.countFiveStars = countFiveStars;
    this.totalRating = countOneStar + countTwoStars + countThreeStars + countFourStars + countFiveStars;
    this.averageRating = totalRating == 0 ? 0
        : (countOneStar + countTwoStars * 2 + countThreeStars * 3 + countFourStars * 4 + countFiveStars * 5) / (float) totalRating;
  }

  public int getTotalRating() {
    return totalRating;
  }

  public float getAverageRating() {
    return averageRating;
  }

}
