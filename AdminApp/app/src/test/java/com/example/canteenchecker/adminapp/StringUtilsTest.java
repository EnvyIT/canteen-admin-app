package com.example.canteenchecker.adminapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.canteenchecker.common.StringUtils;
import java.text.ParseException;
import org.junit.jupiter.api.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class StringUtilsTest {

  @Test
  public void isNullOrEmpty_nullText_ShouldReturnTrue() {
    assertTrue(StringUtils.isNullOrEmpty(null));
  }

  @Test
  public void isNullOrEmpty_emptyText_ShouldReturnTrue() {
    assertTrue(StringUtils.isNullOrEmpty(""));
  }

  @Test
  public void tisNullOrEmpty_nonEmptyText_ShouldReturnFalse() {
    assertFalse(StringUtils.isNullOrEmpty("Hello, CanteenAdmin!"));
  }

  @Test
  public void isNullOrWhiteSpace_nullText_ShouldReturnTrue() {
    assertTrue(StringUtils.isNullOrWhiteSpace(null));
  }

  @Test
  public void isNullOrWhiteSpace_emptyText_ShouldReturnTrue() {
    assertTrue(StringUtils.isNullOrWhiteSpace(""));
  }

  @Test
  public void isNullOrWhiteSpace_whiteSpaceText_ShouldReturnTrue() {
    assertTrue(StringUtils.isNullOrWhiteSpace("    "));
  }

  @Test
  public void isNullOrWhiteSpace_nonEmptyText_ShouldReturnFalse() {
    assertFalse(StringUtils.isNullOrWhiteSpace("Hello, CanteenAdmin!"));
  }

  @Test
  public void validDateToFormattedString_ShouldReturnFormattedDate() throws ParseException {
    String formattedDate = StringUtils.toFormattedDate("2020-12-24T20:46:40.6971438");
    final String expected = "24.12.2020 20:46:40";
    assertEquals(expected, formattedDate);
  }

  @Test
  public void invalidDateToFormattedString_ShouldThrowParseException(){
    assertThrows(ParseException.class, () -> StringUtils.toFormattedDate("2020-1.1234"));
  }

  @Test
  public void nullDateToFormattedString_ShouldThrowParseException() throws ParseException{
    assertNull(StringUtils.toFormattedDate(null));
  }

}