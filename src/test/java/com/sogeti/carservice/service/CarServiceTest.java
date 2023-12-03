package com.sogeti.carservice.service;
import com.sogeti.carservice.client.IAMFeignClient;
import com.sogeti.carservice.dto.CarDTO;
import com.sogeti.carservice.exception.TokenValidationException;
import com.sogeti.carservice.model.Car;
import com.sogeti.carservice.repository.CarRepository;
import com.sogeti.carservice.utility.CarMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private IAMFeignClient iamFeignClient;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCars() {
        Car carOne = Car.builder().id(1L).make("Toyota").model("Camry").version("2022").numberOfDoors(4)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
        Car carTwo = Car.builder().id(2L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
        List<Car> cars = Arrays.asList(carOne, carTwo);

        when(carRepository.findAll()).thenReturn(cars);

        List<CarDTO> result = carService.getAllCars();

        assertEquals(2, result.size());
        assertEquals(cars.size(), result.size());
    }

    @Test
    void testCreateCar() {
        Car car = Car.builder().id(1L).make("Toyota").model("Camry").version("2022").numberOfDoors(4)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        CarDTO carDTO = CarDTO.builder().id(1L).make("Toyota").model("Camry").version("2022").numberOfDoors(4)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        when(carRepository.save(any())).thenReturn(car);
        when(carMapper.carToCarDTO(car)).thenReturn(carDTO);

        CarDTO result = carService.createCar(carDTO);
        assertEquals(carDTO, result);
    }

   @Test
    void testGetCarById() {
       Car car = Car.builder().id(2L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
               .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
       CarDTO carDTO = CarDTO.builder().id(2L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
               .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        when(carRepository.findById(2L)).thenReturn(Optional.of(car));
        when(carMapper.carToCarDTO(car)).thenReturn(carDTO);

        CarDTO result = carService.getCarById(2L);

        assertEquals(carDTO, result);
    }

    @Test
    void testUpdateCar() {
        Car existingCar = Car.builder().id(4L).make("Suzuki").model("Mustang").version("2022").numberOfDoors(5)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
        CarDTO carDTO = CarDTO.builder().id(4L).make("Suzuki").model("Mustang").version("2022").numberOfDoors(5)
                .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        when(carRepository.findById(4L)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any())).thenReturn(existingCar);
        when(carMapper.carToCarDTO(existingCar)).thenReturn(carDTO);

        CarDTO result = carService.updateCar(4L, carDTO);

        assertEquals(carDTO, result);
    }

    @Test
    void testDeleteCar() {
        Long carId = 1L;

        assertDoesNotThrow(() -> carService.deleteCar(carId));

        verify(carRepository, times(1)).deleteById(carId);
    }

    @Test
    void testIsValidTokenSuccess() {
        String token = "validToken";
        ResponseEntity<String> successResponse = new ResponseEntity<>("Valid token", HttpStatus.OK);

        when(iamFeignClient.validateToken("Bearer " + token)).thenReturn(successResponse);

        assertDoesNotThrow(() -> carService.isValidToken(token));
    }

    @Test
    void testIsValidTokenFailure() {
        String token = "invalidToken";
        ResponseEntity<String> unauthorizedResponse = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        when(iamFeignClient.validateToken("Bearer " + token)).thenThrow(FeignException.class);

        TokenValidationException exception = assertThrows(TokenValidationException.class,
                () -> carService.isValidToken(token));

        assertEquals("Token validation failed", exception.getMessage());
    }
}
