package com.example.canteenchecker.proxy;

import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.core.CanteenReviewStatistics;
import com.example.canteenchecker.core.ReviewData;
import com.example.canteenchecker.dto.CanteenDetailsDto;
import com.example.canteenchecker.dto.CanteenReviewStatisticsDto;
import com.example.canteenchecker.dto.ReviewDataDto;
import com.example.canteenchecker.infrastructure.mapper.CanteenDetailsMapper;
import com.example.canteenchecker.infrastructure.mapper.CanteenReviewStatisticsMapper;
import com.example.canteenchecker.infrastructure.mapper.ReviewDataMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ServiceProxyImpl implements ServiceProxy {

  private static final String SERVICE_BASE_URL = "https://moc5.projekte.fh-hagenberg.at/CanteenChecker/api/admin/";

  private final Proxy canteenProxy = new Retrofit.Builder()
      .baseUrl(SERVICE_BASE_URL)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(Proxy.class);

  @Override
  public String authenticate(String username, String password) throws IOException {
    return canteenProxy.postAuthenticate(username, password)
        .execute()
        .body();
  }

  @Override
  public CanteenDetails getCanteen(String authenticationToken) throws IOException {
    CanteenDetailsDto canteenDetailsDto = canteenProxy.getCanteen(setBearer(authenticationToken))
        .execute()
        .body();
    return canteenDetailsDto == null ? null : CanteenDetailsMapper.Instance.map(canteenDetailsDto);
  }

  @Override
  public CanteenReviewStatistics getReviewStatistics(String authenticationToken) throws IOException {
    CanteenReviewStatisticsDto canteenReviewStatisticsDto = canteenProxy
        .getReviewStatistics(setBearer(authenticationToken))
        .execute()
        .body();
    return canteenReviewStatisticsDto == null ? null : CanteenReviewStatisticsMapper.Instance.map(canteenReviewStatisticsDto);
  }

  @Override
  public boolean updateCanteenData(String authenticationToken, CanteenDetails canteenDetails) throws IOException {
    return canteenProxy
        .updateCanteenData(setBearer(authenticationToken), canteenDetails.getName(), canteenDetails.getAddress(),
            canteenDetails.getWebsite(), canteenDetails.getPhoneNumber())
        .execute()
        .isSuccessful();
  }

  @Override
  public boolean updateDish(String authenticationToken, CanteenDetails canteenDetails) throws IOException {
    return canteenProxy
        .updateDish(setBearer(authenticationToken), canteenDetails.getDish(), canteenDetails.getDishPrice())
        .execute()
        .isSuccessful();
  }

  @Override
  public boolean updateWaitingTime(String authenticationToken, int waitingTime) throws IOException {
    return canteenProxy
        .updateWaitingTime(setBearer(authenticationToken), waitingTime)
        .execute()
        .isSuccessful();
  }

  @Override
  public Collection<ReviewData> getReviews(String authenticationToken) throws IOException {
    Collection<ReviewDataDto> reviewDataDtos = canteenProxy
        .getReviews(setBearer(authenticationToken))
        .execute()
        .body();
    List<ReviewData> reviewData = new ArrayList<>();
    if (reviewDataDtos != null) {
      for (ReviewDataDto reviewDataDto : reviewDataDtos) {
        reviewData.add(ReviewDataMapper.Instance.map(reviewDataDto));
      }
    }
    return reviewData;
  }

  @Override
  public boolean deleteReviewById(String authenticationToken, String reviewId) throws IOException {
    return canteenProxy.deleteReviewById(setBearer(authenticationToken), reviewId)
        .execute()
        .isSuccessful();
  }

  private String setBearer(String authenticationToken) {
    return String.format("Bearer %s", authenticationToken);
  }

}
