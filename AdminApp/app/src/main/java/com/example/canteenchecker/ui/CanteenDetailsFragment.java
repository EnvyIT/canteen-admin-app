package com.example.canteenchecker.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.common.ControlUtil;
import com.example.canteenchecker.common.StringUtils;
import com.example.canteenchecker.core.Broadcasting;
import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.infrastructure.CanteenAdminApplication;
import com.example.canteenchecker.proxy.ServiceProxyFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

public class CanteenDetailsFragment extends Fragment {

  private static final float DEFAULT_MAP_ZOOM_FACTOR = 15;
  private static final String TAG = CanteenDetailsFragment.class.getSimpleName();
  private static final String CANTEEN_ID_KEY = "canteenId";

  private View viewProgress;
  private ScrollView viewContent;
  private EditText editTextName;
  private EditText editTextLocation;
  private EditText editTextDish;
  private EditText editTextDishPrice;
  private EditText editTextWaitingTime;
  private ProgressBar progressBarWaitingTime;
  private SupportMapFragment mapFragmentMap;
  private EditText editPhoneNumber;
  private EditText editWebsite;
  private FloatingActionButton floatingActionButtonSave;
  private final BroadcastReceiver broadcastReceiver = getBroadcastReceiver();

  @Getter
  @Setter
  private String canteenId;

  @Getter
  @Setter
  private CanteenDetails oldCanteenDetails;

  private BroadcastReceiver getBroadcastReceiver() {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (canteenId != null && canteenId.equals(Broadcasting.extractCanteenId(intent))) {
          Log.i(TAG, String.format("Received message - reloading canteen details"));
          loadCanteenDetails();
        }
      }
    };
  }

  public static Fragment getInstance(String canteenId) {
    CanteenDetailsFragment fragment = new CanteenDetailsFragment();
    Bundle bundle = new Bundle();
    bundle.putSerializable(CANTEEN_ID_KEY, canteenId);
    fragment.setArguments(bundle);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_canteen_details, container, false);
    registerBroadcastReceiver();
    setCanteenId(extractCanteenId());
    initializeFields(view);
    inflateReviewStatisticFragment();
    initializeMapFragment();
    loadCanteenDetails();

    floatingActionButtonSave.setOnClickListener(this::handleSave);
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

  private String extractCanteenId() {
    return (String) getArguments().get(CANTEEN_ID_KEY);
  }

  private void initializeMapFragment() {
    mapFragmentMap = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragmentMap);
    if (mapFragmentMap != null) {
      mapFragmentMap.getMapAsync(this::onMapReady);
    }
  }

  private void inflateReviewStatisticFragment() {
    if (getFragmentManager() != null) {
      getFragmentManager()
          .beginTransaction()
          .replace(R.id.linearLayoutReviews, ReviewStatisticFragment.create(getCanteenId()))
          .commit();
    }
  }

  private void initializeFields(View view) {
    viewProgress = view.findViewById(R.id.viewProgress);
    viewContent = view.findViewById(R.id.viewContent);
    editTextName = view.findViewById(R.id.editTextName);
    editTextLocation = view.findViewById(R.id.editTextLocation);
    editTextDish = view.findViewById(R.id.editTextDish);
    editTextDishPrice = view.findViewById(R.id.editTextDishPrice);
    editTextWaitingTime = view.findViewById(R.id.editTextWaitingTime);
    editPhoneNumber = view.findViewById(R.id.editPhoneNumber);
    editWebsite = view.findViewById(R.id.editWebsite);
    floatingActionButtonSave = view.findViewById(R.id.floatingActionButtonSave);
    progressBarWaitingTime = view.findViewById(R.id.progressBarWaitingTime);
  }

  private void onMapClick(LatLng latitudeLongitude) throws IOException {
    editTextLocation.setText(getAddress(latitudeLongitude));
  }

  private String getAddress(LatLng latitudeLongitude) throws IOException {
    Geocoder geocoder = new Geocoder(getContext());
    Address address = geocoder.getFromLocation(latitudeLongitude.latitude, latitudeLongitude.longitude, 1)
        .get(0);
    return address.getAddressLine(0);
  }

  @SuppressLint("StaticFieldLeak")
  private void handleSave(View view) {
    new AsyncTask<Void, Void, Boolean>() {

      @Override
      protected Boolean doInBackground(Void... voids) {
        String authenticationToken = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
        CanteenDetails updatedCanteenDetails = getUpdatedCanteen();
        boolean success = true;
        if (hasCanteenDataChanged(updatedCanteenDetails)) {
          success = success && tryUpdateCanteenData(authenticationToken, updatedCanteenDetails);
        }
        if (hasDishChanged(updatedCanteenDetails)) {
          success = success && tryUpdateDish(authenticationToken, updatedCanteenDetails);
        }
        if (hasWaitingTimeChanged(updatedCanteenDetails)) {
          success = success && tryUpdateWaitingTime(authenticationToken, updatedCanteenDetails);
        }
        return success;
      }

      @Override
      protected void onPostExecute(Boolean isSuccess) {
        if (isSuccess != null && isSuccess.booleanValue()) {
          Toast.makeText(getContext(), R.string.message_canteen_data_saved, Toast.LENGTH_SHORT).show();
          LocalBroadcastManager
              .getInstance(Objects.requireNonNull(getActivity()))
              .sendBroadcast(Broadcasting.createCanteenChangedBroadcastIntent(getCanteenId()));
        }
      }

    }.execute();
  }

  private boolean hasWaitingTimeChanged(CanteenDetails updatedCanteenDetails) {
    return updatedCanteenDetails.getWaitingTime() != getOldCanteenDetails().getWaitingTime();
  }

  private boolean hasDishChanged(CanteenDetails updatedCanteenDetails) {
    return !updatedCanteenDetails.getDish().equals(getOldCanteenDetails().getDish()) ||
        updatedCanteenDetails.getDishPrice() != getOldCanteenDetails().getDishPrice();
  }

  private boolean hasCanteenDataChanged(CanteenDetails updatedCanteenDetails) {
    return !updatedCanteenDetails.getName().equals(getOldCanteenDetails().getName()) ||
        !updatedCanteenDetails.getWebsite().equals(getOldCanteenDetails().getWebsite()) ||
        !updatedCanteenDetails.getPhoneNumber().equals(getOldCanteenDetails().getPhoneNumber()) ||
        !updatedCanteenDetails.getAddress().equals(getOldCanteenDetails().getAddress());
  }

  private CanteenDetails getUpdatedCanteen() {
    double dishPrice = 0.0;
    try {
      dishPrice = NumberFormat
          .getCurrencyInstance(Locale.getDefault())
          .parse(StringUtils.toCurrencyFormat(editTextDishPrice.getText().toString()))
          .doubleValue();
    } catch (Exception e) {
      Log.e(TAG, String.format("Failed to parse price for value '%s'", editTextDish.getText().toString()), e);
      //intentionally ignored fallback is 0.0
    }
    return new CanteenDetails(
        getCanteenId(),
        editTextName.getText().toString(),
        editTextLocation.getText().toString(),
        editPhoneNumber.getText().toString(),
        editWebsite.getText().toString(),
        editTextDish.getText().toString(),
        dishPrice,
        "".equals(editTextWaitingTime.getText().toString()) ? 0 : Integer.parseInt(editTextWaitingTime.getText().toString())
    );
  }

  @SuppressLint("StaticFieldLeak")
  private void loadCanteenDetails() {
    new AsyncTask<Void, Void, CanteenDetails>() {

      @Override
      protected CanteenDetails doInBackground(Void... parameters) {
        return getCanteenDetails();
      }

      @Override
      protected void onPostExecute(CanteenDetails canteenDetails) {
        if (canteenDetails != null) {
          applyDetails(canteenDetails);
          showContent();
          setLocation(canteenDetails);
          setOldCanteenDetails(canteenDetails);
        }
      }
    }.execute();
  }

  private CanteenDetails getCanteenDetails() {
    try {
      String authenticationToken = ((CanteenAdminApplication) getActivity().getApplication()).getAuthenticationToken();
      return ServiceProxyFactory.create().getCanteen(authenticationToken);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s", getResources().getString(R.string.message_fechting_cateen_data_failed)), exception);
    }

    return null;
  }


  @SuppressLint("StaticFieldLeak")
  private void setLocation(CanteenDetails canteenDetails) {
    new AsyncTask<String, Void, LatLng>() {
      @Override
      protected LatLng doInBackground(String... parameters) {
        LatLng location = null;
        Geocoder geocoder = new Geocoder(getContext());
        try {
          List<Address> addresses = parameters[0] == null ? null : geocoder.getFromLocationName(parameters[0], 1);
          if (addresses != null && !addresses.isEmpty()) {
            Address address = addresses.get(0);
            location = new LatLng(address.getLatitude(), address.getLongitude());
          } else {
            Log.w(TAG, String.format("%s '%s'", getResources().getString(R.string.message_no_location_found), parameters[0]));
          }
        } catch (IOException e) {
          Log.e(TAG, String.format("%s '%s' ", getResources().getString(R.string.message_location_lookup_failed), parameters[0]), e);
        }
        return location;
      }

      @Override
      protected void onPostExecute(LatLng latLng) {
        mapFragmentMap.getMapAsync(googleMap -> {
          if (latLng != null) {
            updateMarker(latLng, googleMap);
          } else {
            animateToDefault(googleMap);
          }
        });
      }
    }.execute(canteenDetails.getAddress());
  }

  private void animateToDefault(GoogleMap googleMap) {
    googleMap.clear();
    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 0));
  }

  private void updateMarker(LatLng latLng, GoogleMap googleMap) {
    googleMap.clear();
    googleMap.addMarker(new MarkerOptions().position(latLng));
    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_FACTOR));
  }

  private void showContent() {
    viewProgress.setVisibility(View.GONE);
    viewContent.setVisibility(View.VISIBLE);
  }

  private void applyDetails(CanteenDetails canteenDetails) {
    editTextName.setText(canteenDetails.getName());
    editTextLocation.setText(canteenDetails.getAddress());
    editTextDish.setText(canteenDetails.getDish());
    editPhoneNumber.setText(canteenDetails.getPhoneNumber());
    editWebsite.setText(canteenDetails.getWebsite());
    editTextDishPrice.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(canteenDetails.getDishPrice()));
    editTextWaitingTime.setText(String.format("%s", canteenDetails.getWaitingTime()));
    progressBarWaitingTime.setProgress(canteenDetails.getWaitingTime());
    ControlUtil.setDecimalSeparator(editTextDishPrice);
  }

  private void onMapReady(GoogleMap googleMap) {
    UiSettings uiSettings = googleMap.getUiSettings();
    googleMap.setOnCameraMoveStartedListener(this::onCameraMoveStarted);
    googleMap.setOnCameraIdleListener(this::onCameraIdle);
    uiSettings.setAllGesturesEnabled(true);
    uiSettings.setZoomControlsEnabled(true);
    googleMap.setOnMapClickListener(latitudeLongitude -> handleMapClick(latitudeLongitude, googleMap));
  }

  private void onCameraIdle() {
    //disallow scrolling on scroll viewer while scrolling on google maps
    viewContent.requestDisallowInterceptTouchEvent(false);
  }

  private void onCameraMoveStarted(int i) {
    //allow scrolling on scroll viewer when stop scrolling on google maps
    viewContent.requestDisallowInterceptTouchEvent(true);
  }

  private void handleMapClick(LatLng latitudeLongitude, GoogleMap googleMap) {
    try {
      onMapClick(latitudeLongitude);
      updateMarker(latitudeLongitude, googleMap);
    } catch (IOException ioException) {
      Log.e(TAG, getResources().getString(R.string.message_map_click_error), ioException);
    }
  }


  private boolean tryUpdateDish(String authenticationToken, CanteenDetails canteenDetails) {
    try {
      return ServiceProxyFactory.create().updateDish(authenticationToken, canteenDetails);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s", getResources().getString(R.string.message_update_dish_failed)), exception);
    }
    return false;
  }

  private boolean tryUpdateWaitingTime(String authenticationToken, CanteenDetails canteenDetails) {
    try {
      return ServiceProxyFactory.create().updateWaitingTime(authenticationToken, canteenDetails.getWaitingTime());
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s", getResources().getString(R.string.message_update_waiting_time_failed)), exception);
    }
    return false;
  }

  private boolean tryUpdateCanteenData(String authenticationToken, CanteenDetails canteenDetails) {
    try {
      return ServiceProxyFactory.create().updateCanteenData(authenticationToken, canteenDetails);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s", getResources().getString(R.string.message_update_canteen_data_failed)), exception);
    }
    return false;
  }

}