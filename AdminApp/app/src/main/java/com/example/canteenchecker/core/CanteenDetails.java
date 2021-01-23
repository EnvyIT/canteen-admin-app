package com.example.canteenchecker.core;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanteenDetails implements Serializable {

  private final String id;
  private final String name;
  private final String address;
  private final String phoneNumber;
  private final String website;
  private final String dish;
  private final double dishPrice;
  private final int waitingTime;

}
