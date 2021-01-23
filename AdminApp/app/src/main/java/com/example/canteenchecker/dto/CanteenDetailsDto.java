package com.example.canteenchecker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CanteenDetailsDto {

  private final String id;
  private final String name;
  private final String address;
  private final String phoneNumber;
  private final String website;
  private final String dish;
  private final double dishPrice;
  private final int waitingTime;

}
