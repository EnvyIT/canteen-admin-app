package com.example.canteenchecker.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.core.Broadcasting;
import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.core.ReviewData;
import com.example.canteenchecker.infrastructure.CanteenAdminApplication;
import com.example.canteenchecker.proxy.ServiceProxyFactory;
import com.example.canteenchecker.ui.adapter.ReviewsAdapter;
import com.example.canteenchecker.ui.helper.RecyclerItemTouchHelper;
import com.example.canteenchecker.ui.helper.RecyclerItemTouchHelperListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public class CanteenReviewFragment extends Fragment implements RecyclerItemTouchHelperListener {

  private static final String TAG = CanteenReviewFragment.class.getSimpleName();
  private static final String CANTEEN_ID_KEY = "canteenId";

  private SwipeRefreshLayout swipeRefreshLayout;
  private final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
  private final BroadcastReceiver broadcastReceiver = getBroadcastReceiver();

  @Getter
  @Setter
  private String canteenId;


  public static Fragment getInstance(String canteenId) {
    CanteenReviewFragment fragment = new CanteenReviewFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(CANTEEN_ID_KEY, canteenId);
    fragment.setArguments(bundle);
    return fragment;
  }

  private BroadcastReceiver getBroadcastReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (canteenId != null && canteenId.equals(Broadcasting.extractCanteenId(intent))) {
          Log.i(TAG, String.format("Received message - reloading udpate reviews"));
          updateReviews();
        }
      }
    };
  }

  private String extractCanteenId() {
    return (String) getArguments().get(CANTEEN_ID_KEY);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_canteen_review, container, false);
    registerBroadcastReceiver();
    setCanteenId(extractCanteenId());
    RecyclerView reviewRecyclerView = view.findViewById(R.id.recyclerViewReviews);
    reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    reviewRecyclerView.setAdapter(reviewsAdapter);

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
    new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(reviewRecyclerView);

    swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setOnRefreshListener(this::updateReviews);
    updateReviews();
    return view;
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

  @SuppressLint("StaticFieldLeak")
  private void updateReviews() {
    swipeRefreshLayout.setRefreshing(true);
    new AsyncTask<Void, Void, Collection<ReviewData>>() {

      @Override
      protected Collection<ReviewData> doInBackground(Void... voids) {
        return getReviewData();
      }

      @Override
      protected void onPostExecute(Collection<ReviewData> reviewData) {
        reviewsAdapter.display(reviewData);
        if (reviewData != null) {
          Log.i(TAG, String.format("%s: %d", getResources().getString(R.string.message_review_data_loaded), reviewData.size()));
        }
        swipeRefreshLayout.setRefreshing(false);
      }

    }.execute();

  }

  private Collection<ReviewData> getReviewData() {
    try {
      String authenticationToken = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
      Log.i(TAG, getResources().getString(R.string.message_fetching_review_data));
      return ServiceProxyFactory.create().getReviews(authenticationToken);
    } catch (IOException exception) {
      Log.e(TAG, getResources().getString(R.string.message_fetching_review_data_failed), exception);
    }
    return null;
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction, int position) {
    if (viewHolder instanceof ReviewsAdapter.ViewHolder) {
      removeReview(reviewsAdapter.get(position).getId(), reviewsAdapter, position);
    }
  }


  @SuppressLint("StaticFieldLeak")
  private void removeReview(String reviewId, ReviewsAdapter reviewsAdapter, int position) {
    new AsyncTask<String, Void, Boolean>() {

      @Override
      protected Boolean doInBackground(String... parameters) {
        Log.i(TAG, getResources().getString(R.string.message_delete_review));
        return deleteReview(parameters[0]);
      }

      @Override
      protected void onPostExecute(Boolean isRemoved) {
        if (isRemoved) {
          reviewsAdapter.remove(position);
          Toast.makeText(getContext(), getResources().getString(R.string.message_removed_review), Toast.LENGTH_SHORT).show();
          LocalBroadcastManager
              .getInstance(Objects.requireNonNull(getActivity()))
              .sendBroadcast(Broadcasting.createCanteenChangedBroadcastIntent(getCanteenId()));
        } else {
          Toast.makeText(getContext(), getResources().getString(R.string.message_could_not_remove_review), Toast.LENGTH_SHORT).show();
        }
      }

    }.execute(reviewId);
  }

  private Boolean deleteReview(String reviewId) {
    try {
      String authenticationToken = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
      return ServiceProxyFactory.create().deleteReviewById(authenticationToken, reviewId);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s '%s'", getResources().getString(R.string.message_delete_review_failed), reviewId),
          exception);
    }
    return false;
  }

}