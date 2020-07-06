package com.whoppr.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  String addressLine1;
  String addressLine2;
  String city;
  String state;
  String postalCode;
  String countryCode;
}
