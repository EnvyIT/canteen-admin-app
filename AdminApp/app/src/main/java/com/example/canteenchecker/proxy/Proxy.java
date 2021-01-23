package com.example.canteenchecker.proxy;

import com.example.canteenchecker.dto.CanteenDataDto;
import com.example.canteenchecker.dto.CanteenDetailsDto;
import com.example.canteenchecker.dto.CanteenReviewStatisticsDto;
import com.example.canteenchecker.dto.ReviewDataDto;
import java.util.Collection;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Proxy {

  @POST("authenticate")
  Call<String> postAuthenticate(@Query("userName") String userName, @Query("password") String password);

  @GET("canteen")
  Call<CanteenDetailsDto> getCanteen(@Header("Authorization") String authenticationToken);

  @GET("canteen/review-statistics")
  Call<CanteenReviewStatisticsDto> getReviewStatistics(@Header("Authorization") String authenticationToken);

  @PUT("canteen/data")
  Call<Boolean> updateCanteenData(@Header("Authorization") String authenticationToken, @Query("name") String name,
      @Query("address") String address, @Query("website") String website, @Query("phoneNumber") String phoneNumber);

  @PUT("canteen/dish")
  Call<Boolean> updateDish(@Header("Authorization") String authenticationToken, @Query("dish") String dish,
      @Query("dishPrice") double dishPrice);

  @PUT("canteen/waiting-time")
  Call<Boolean> updateWaitingTime(@Header("Authorization") String authenticationToken,
      @Query("waitingTime") int waitingTime);

  @GET("canteen/reviews")
  Call<Collection<ReviewDataDto>> getReviews(@Header("Authorization") String authenticationToken);

  @DELETE("canteen/reviews/{reviewId}")
  Call<Boolean> deleteReviewById(@Header("Authorization") String authenticationToken, @Path("reviewId") String reviewId);

}