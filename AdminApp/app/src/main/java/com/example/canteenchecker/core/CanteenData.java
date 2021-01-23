package com.example.canteenchecker.core;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanteenData implements Serializable {

  private final String id;
  private final String name;
  private final String dish;
  private final double  dishPrice;
  private final double  averageRating;

}
