package com.sogeti.carservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {
    private Long id;
    private String make;
    private String model;
    private String version;
    private int numberOfDoors;
    private double co2Emission;
    private double grossPrice;
    private double nettPrice;
}
