package com.example.canteenchecker.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;


public class TabAdapter extends FragmentPagerAdapter {

  private final List<Fragment> fragments = new ArrayList<>();
  private final List<String> titles = new ArrayList<>();

  public TabAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  @NonNull
  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  public List<Fragment> getItems() {
    return fragments;
  }

  public void addFragment(Fragment fragment, String title) {
    fragments.add(fragment);
    titles.add(title);
  }

  public boolean isEmpty() {
    return fragments.isEmpty();
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return titles.get(position);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }



}