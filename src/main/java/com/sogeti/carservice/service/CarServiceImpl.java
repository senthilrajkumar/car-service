package com.sogeti.carservice.service;

import com.sogeti.carservice.client.IAMFeignClient;
import com.sogeti.carservice.dto.CarDTO;
import com.sogeti.carservice.exception.TokenValidationException;
import com.sogeti.carservice.model.Car;
import com.sogeti.carservice.repository.CarRepository;
import com.sogeti.carservice.utility.CarMapper;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    @Qualifier("iamFeignClient")
    private final IAMFeignClient iamFeignClient;

    @Override
    public List<CarDTO> getAllCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(carMapper::carToCarDTO)
                .toList();
    }

    @Override
    public CarDTO createCar(CarDTO carDTO) {
        Car car = carMapper.carDTOToCar(carDTO);
        return carMapper.carToCarDTO(carRepository.save(car));
    }

    @Override
    public CarDTO getCarById(Long id) {
        Car car = carRepository.findById(id).orElse(null);
        return (car != null) ? carMapper.carToCarDTO(car) : null;
    }

    @Override
    public CarDTO updateCar(Long id, CarDTO carDTO) {
        Car existingCar = carRepository.findById(id).orElse(null);
        if (existingCar != null) {
            Car updatedCar = carRepository.save(carMapper.carDTOToCar(carDTO));
            return carMapper.carToCarDTO(updatedCar);
        }
        return null;
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            String authorizationHeader = "Bearer " + token;
            // Call the token-validation endpoint of iam-service using Feign Client
            ResponseEntity<String> response = iamFeignClient.validateToken(authorizationHeader);
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("User is not authenticated");
                throw new TokenValidationException("Token validation failed");
            }
        } catch(FeignException e) {
            log.error("User is not authenticated");
            throw new TokenValidationException("Token validation failed");
        }
        log.info("User is successfully authenticated");
        return true;
    }
}
