package com.example.canteenchecker.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.canteenchecker.adminapp.R;
import com.example.canteenchecker.core.CanteenDetails;
import com.example.canteenchecker.infrastructure.CanteenAdminApplication;
import com.example.canteenchecker.proxy.ServiceProxyFactory;
import com.example.canteenchecker.ui.adapter.TabAdapter;
import com.google.android.material.tabs.TabLayout;
import java.io.IOException;

public class TabBarActivity extends AppCompatActivity {

  private static final String TAG = TabBarActivity.class.getSimpleName();

  private TabAdapter tabAdapter;
  private ViewPager viewPager;
  private TabLayout tabLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tab_bar);
    initializeFields();
    tabAdapter = new TabAdapter(getSupportFragmentManager());
    loadCanteenDetails();
  }

  private void initializeFields() {
    viewPager = findViewById(R.id.viewPager);
    tabLayout = findViewById(R.id.tabLayout);
  }

  @Override
  public void onBackPressed() {
    this.moveTaskToBack(true);
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
        String canteenId = canteenDetails.getId();
        tabAdapter.addFragment(CanteenDetailsFragment.getInstance(canteenId), getResources().getString(R.string.navigation_canteen));
        tabAdapter.addFragment(CanteenReviewFragment.getInstance(canteenId), getResources().getString(R.string.navigation_reviews));
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
      }

    }.execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_tab_bar, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
    final int id = menuItem.getItemId();
    if (id == R.id.menu_logout) {
      logout();
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  private void logout() {
    ((CanteenAdminApplication) getApplication()).clearAuthenticationToken();
    Intent loginIntent = new Intent(this, LoginActivity.class);
    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(loginIntent);
  }

  private CanteenDetails getCanteenDetails() {
    try {
      String authenticationToken = ((CanteenAdminApplication) getApplication()).getAuthenticationToken();
      return ServiceProxyFactory.create().getCanteen(authenticationToken);
    } catch (IOException exception) {
      Log.e(TAG, String.format("%s", getResources().getString(R.string.message_fechting_cateen_data_failed)), exception);
    }
    return null;
  }

}