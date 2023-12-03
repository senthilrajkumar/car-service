package com.sogeti.carservice.service;

import com.sogeti.carservice.dto.CarDTO;

import java.util.List;

public interface CarService {
    List<CarDTO> getAllCars();
    CarDTO createCar(CarDTO carDTO);
    CarDTO getCarById(Long id);
    CarDTO updateCar(Long id, CarDTO carDTO);
    void deleteCar(Long id);
    boolean isValidToken(String token);
}
