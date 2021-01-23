package com.example.canteenchecker.proxy;

import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.core.CanteenReviewStatistics;
import com.example.canteenchecker.core.ReviewData;
import java.io.IOException;
import java.util.Collection;

public interface ServiceProxy {

  String authenticate(String username, String password) throws IOException;

  CanteenDetails getCanteen(String authenticationToken) throws IOException;

  CanteenReviewStatistics getReviewStatistics(String authenticationToken) throws IOException;

  boolean updateCanteenData(String authenticationToken, CanteenDetails canteenDetails) throws IOException;

  boolean updateDish(String authenticationToken, CanteenDetails canteenDetails) throws IOException;

  boolean updateWaitingTime(String authenticationToken, int waitingTime) throws IOException;

  Collection<ReviewData> getReviews(String authenticationToken) throws IOException;

  boolean deleteReviewById(String authenticationToken, String reviewId) throws IOException;

}
