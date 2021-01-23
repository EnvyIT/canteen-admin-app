package com.example.canteenchecker.core;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewData implements Serializable {

  private final String id;
  private final String creationDate;
  private final String creator;
  private final int rating;
  private final String remark;

}
