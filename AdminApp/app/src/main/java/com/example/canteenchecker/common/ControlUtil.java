package com.example.canteenchecker.common;

import android.text.method.DigitsKeyListener;
import android.widget.EditText;
import java.text.DecimalFormatSymbols;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class ControlUtil {

  public static void setDecimalSeparator(EditText priceField) {
    char separator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
    priceField.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
  }

}
