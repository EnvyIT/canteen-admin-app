package com.example.canteenchecker.core;

import android.content.Intent;
import android.content.IntentFilter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Broadcasting {

  private static final String CANTEEN_CHANGED_INTENT_ACTION = "CanteenChanged";
  private static final String CANTEEN_CHANGED_INTENT_CANTEEN_ID = "CanteenId";

  public static Intent createCanteenChangedBroadcastIntent(String canteenId) {
    return createIntent(canteenId);
  }

  public static String extractCanteenId(Intent intent) {
    return intent.getStringExtra(CANTEEN_CHANGED_INTENT_CANTEEN_ID);
  }

  public static IntentFilter createCanteenChangedBroadcastFilter() {
    return new IntentFilter(CANTEEN_CHANGED_INTENT_ACTION);
  }

  private static Intent createIntent(String value) {
    Intent intent = new Intent(Broadcasting.CANTEEN_CHANGED_INTENT_ACTION);
    intent.putExtra(Broadcasting.CANTEEN_CHANGED_INTENT_CANTEEN_ID, value);
    return intent;
  }



}
