package com.example.canteenchecker.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.core.Broadcasting;
import com.example.canteenchecker.core.CanteenReviewStatistics;
import com.example.canteenchecker.infrastructure.CanteenAdminApplication;
import com.example.canteenchecker.proxy.ServiceProxyFactory;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public class ReviewStatisticFragment extends Fragment {

  private static final String TAG = ReviewStatisticFragment.class.getSimpleName();
  private static final String CANTEEN_ID_KEY = "CanteenId";

  private TextView textAverageRating;
  private RatingBar ratingBarAverageRating;
  private TextView textTotalRating;
  private ProgressBar progressBarRatingOne;
  private ProgressBar progressBarRatingTwo;
  private ProgressBar progressBarRatingThree;
  private ProgressBar progressBarRatingFour;
  private ProgressBar progressBarRatingFive;
  private final BroadcastReceiver broadcastReceiver = getBroadcastReceiver();

  @Getter
  @Setter
  private String canteenId;

  public static Fragment create(String canteenId) {
    ReviewStatisticFragment reviewsFragment = new ReviewStatisticFragment();
    Bundle arguments = new Bundle();
    arguments.putString(CANTEEN_ID_KEY, canteenId);
    reviewsFragment.setArguments(arguments);
    return reviewsFragment;
  }

  private BroadcastReceiver getBroadcastReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (canteenId != null && canteenId.equals(Broadcasting.extractCanteenId(intent))) {
          Log.i(TAG, String.format("Received message - reloading canteen statistic"));
          updateReviews();
        }
      }
    };
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_statistic_review, container, false);
    setCanteenId(extractCanteenId());
    registerBroadcastReceiver();
    initializeFields(view);
    updateReviews();
    return view;
  }

  private void initializeFields(View view) {
    textAverageRating = view.findViewById(R.id.txvAverageRating);
    ratingBarAverageRating = view.findViewById(R.id.rtbAverageRating);
    textTotalRating = view.findViewById(R.id.txvTotalRatings);
    progressBarRatingOne = view.findViewById(R.id.prbRatingsOne);
    progressBarRatingTwo = view.findViewById(R.id.prbRatingsTwo);
    progressBarRatingThree = view.findViewById(R.id.prbRatingsThree);
    progressBarRatingFour = view.findViewById(R.id.prbRatingsFour);
    progressBarRatingFive = view.findViewById(R.id.prbRatingsFive);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    LocalBroadcastManager
        .getInstance(Objects.requireNonNull(getActivity()))
        .unregisterReceiver(broadcastReceiver);
  }

  private void registerBroadcastReceiver() {
    LocalBroadcastManager
        .getInstance(Objects.requireNonNull(getActivity()))
        .registerReceiver(broadcastReceiver, Broadcasting.createCanteenChangedBroadcastFilter());
  }

  @SuppressWarnings("StaticFieldLeak")
  private void updateReviews() {
    new AsyncTask<String, Void, CanteenReviewStatistics>() {

      @Override
      protected CanteenReviewStatistics doInBackground(String... parameters) {
        return getReviewStatistic(parameters[0]);
      }

      @Override
      protected void onPostExecute(CanteenReviewStatistics reviewStatistics) {
        if (reviewStatistics != null) {
          setReviewStatisticFields(reviewStatistics);
        } else {
          setFieldsToDefaultValues();
        }
      }
    }.execute(getCanteenId());
  }

  private void setFieldsToDefaultValues() {
    textAverageRating.setText(null);
    ratingBarAverageRating.setRating(0);
    textTotalRating.setText(null);
    progressBarRatingOne.setMax(1);
    progressBarRatingOne.setProgress(0);
    progressBarRatingTwo.setMax(1);
    progressBarRatingTwo.setProgress(0);
    progressBarRatingThree.setMax(1);
    progressBarRatingThree.setProgress(0);
    progressBarRatingFour.setMax(1);
    progressBarRatingFour.setProgress(0);
    progressBarRatingFive.setMax(1);
    progressBarRatingFive.setProgress(0);
  }

  private void setReviewStatisticFields(CanteenReviewStatistics reviewStatistics) {
    textAverageRating.setText(NumberFormat.getNumberInstance().format(reviewStatistics.getAverageRating()));
    ratingBarAverageRating.setRating(reviewStatistics.getAverageRating());
    textTotalRating.setText(NumberFormat.getNumberInstance().format(reviewStatistics.getTotalRating()));
    progressBarRatingOne.setMax(reviewStatistics.getTotalRating());
    progressBarRatingOne.setProgress(reviewStatistics.getCountOneStar());
    progressBarRatingTwo.setMax(reviewStatistics.getTotalRating());
    progressBarRatingTwo.setProgress(reviewStatistics.getCountTwoStars());
    progressBarRatingThree.setMax(reviewStatistics.getTotalRating());
    progressBarRatingThree.setProgress(reviewStatistics.getCountThreeStars());
    progressBarRatingFour.setMax(reviewStatistics.getTotalRating());
    progressBarRatingFour.setProgress(reviewStatistics.getCountFourStars());
    progressBarRatingFive.setMax(reviewStatistics.getTotalRating());
    progressBarRatingFive.setProgress(reviewStatistics.getCountFiveStars());
  }

  private String extractCanteenId() {
    return getArguments().getString(CANTEEN_ID_KEY);
  }

  private CanteenReviewStatistics getReviewStatistic(String canteenId) {
    try {
      String authenticationToken = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
      return ServiceProxyFactory.create().getReviewStatistics(authenticationToken);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s '%s'", getResources().getString(R.string.message_fetching_review_statistic_failed), canteenId),
          exception);
    }
    return null;
  }
}
