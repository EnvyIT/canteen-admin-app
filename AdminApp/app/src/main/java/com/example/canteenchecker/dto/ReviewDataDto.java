package com.example.canteenchecker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDataDto {

  private final String id;
  private final String creationDate;
  private final String creator;
  private final int rating;
  private final String remark;

}
