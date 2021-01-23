package com.example.canteenchecker.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

  public static boolean isNullOrEmpty(String text){
    return text == null || text.isEmpty();
  }

  public static boolean isNullOrWhiteSpace(String text) {
    return isNullOrEmpty(text) || isWhiteSpace(text);
  }

  public static boolean isWhiteSpace(String text) {
    for(int i = 0; i < text.length(); ++i){
      if(text.charAt(i) != ' ') {
        return false;
      }
    }
    return true;
  }

  public static String toFormattedDate(String dateTime) throws ParseException {
    if(dateTime != null) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      Date date = dateFormat.parse(dateTime);
      SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
      newFormat.setTimeZone(TimeZone.getDefault());
      assert date != null;
      return newFormat.format(date);
    }
    return null;
  }

  public static String toCurrencyFormat(String price) {
    String currencySymbol = Currency.getInstance(Locale.getDefault()).getSymbol();
    if(!price.contains(currencySymbol)) {
      return String.format("%s %s", currencySymbol, price);
    }
    return price;
  }

}
