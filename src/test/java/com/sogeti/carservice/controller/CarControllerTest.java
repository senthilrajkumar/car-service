package com.sogeti.carservice.controller;

import com.sogeti.carservice.dto.CarDTO;
import com.sogeti.carservice.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private CarService carService;

    @LocalServerPort
    private int port;

    @Test
    void testGetAllCarsSuccess() {
        when(carService.isValidToken("validToken")).thenReturn(true);

        List<CarDTO> cars = Arrays.asList(
                CarDTO.builder().id(1L).make("Toyota").model("Camry").version("2022").numberOfDoors(4)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build());
        when(carService.getAllCars()).thenReturn(cars);

        addHeaderInterceptorsForBearer("validToken");
        ResponseEntity<List> response = restTemplate.getForEntity("/api/cars", List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        verify(carService, times(1)).isValidToken("validToken");

        verify(carService, times(1)).getAllCars();
    }

    @Test
    void testGetAllCarsUnauthorized() {
        when(carService.isValidToken("invalidToken")).thenReturn(false);

        addHeaderInterceptorsForBearer("invalidToken");
        ResponseEntity<List> response = restTemplate.getForEntity("/api/cars", List.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(carService, times(1)).isValidToken("invalidToken");

        verify(carService, never()).getAllCars();
    }

    @Test
    void testCreateCarSuccess() {
        when(carService.isValidToken("validToken")).thenReturn(true);

        CarDTO newCar =
                CarDTO.builder().id(null).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
        when(carService.createCar(any(CarDTO.class))).thenReturn(
                CarDTO.builder().id(1L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build());

        addHeaderInterceptorsForBearer("validToken");
        ResponseEntity<CarDTO> response = restTemplate.postForEntity("/api/cars", newCar, CarDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        verify(carService, times(1)).isValidToken("validToken");

        verify(carService, times(1)).createCar(any());
    }

    @Test
    void testCreateCarUnauthorized() {
        when(carService.isValidToken("invalidToken")).thenReturn(false);
        CarDTO newCar =
                CarDTO.builder().id(null).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        addHeaderInterceptorsForBearer("invalidToken");
        ResponseEntity<CarDTO> response = restTemplate.postForEntity("/api/cars", newCar, CarDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(carService, times(1)).isValidToken("invalidToken");

        verify(carService, never()).createCar(any());
    }

    @Test
    void testGetCarByIdSuccess() {
        when(carService.isValidToken("validToken")).thenReturn(true);

        when(carService.getCarById(1L)).thenReturn(
                CarDTO.builder().id(1L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build());

        addHeaderInterceptorsForBearer("validToken");
        ResponseEntity<CarDTO> response = restTemplate.getForEntity("/api/cars/1", CarDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());

        verify(carService, times(1)).isValidToken("validToken");

        verify(carService, times(1)).getCarById(1L);
    }

    @Test
    void testGetCarByIdUnauthorized() {
        when(carService.isValidToken("invalidToken")).thenReturn(false);

        addHeaderInterceptorsForBearer("invalidToken");
        ResponseEntity<CarDTO> response = restTemplate.getForEntity("/api/cars/1", CarDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(carService, times(1)).isValidToken("invalidToken");

        verify(carService, never()).getCarById(anyLong());
    }

    @Test
    void testUpdateCarSuccess() {
        when(carService.isValidToken("validToken")).thenReturn(true);

        CarDTO existingCar =
                CarDTO.builder().id(2L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build();
        when(carService.updateCar(eq(2L), any(CarDTO.class)))
                .thenReturn(
                        CarDTO.builder().id(2L).make("Toyota").model("Camry").version("2022").numberOfDoors(4)
                                .co2Emission(100).grossPrice(20000).nettPrice(18000).build());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("validToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDTO> requestEntity = new HttpEntity<>(existingCar, headers);

        String apiUrl = "/api/cars/2";

        ResponseEntity<CarDTO> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, CarDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2L, response.getBody().getId());

        verify(carService, times(1)).isValidToken("validToken");

        verify(carService, times(1)).updateCar(eq(2L), any(CarDTO.class));
    }

    @Test
    void testUpdateCarUnauthorized() {
        when(carService.isValidToken("invalidToken")).thenReturn(false);

        CarDTO existingCar =
                CarDTO.builder().id(4L).make("Ford").model("Mustang").version("2022").numberOfDoors(5)
                        .co2Emission(100).grossPrice(20000).nettPrice(19000).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalidToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDTO> requestEntity = new HttpEntity<>(existingCar, headers);

        String apiUrl = "/api/cars/4";

        ResponseEntity<CarDTO> response = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, CarDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(carService, times(1)).isValidToken("invalidToken");

        verify(carService, never()).updateCar(anyLong(), any(CarDTO.class));
    }

    @Test
    void testDeleteCarSuccess() {
        when(carService.isValidToken("validToken")).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("validToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDTO> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CarDTO> response = restTemplate.exchange(buildCarURI("/api/cars/4"), HttpMethod.DELETE, requestEntity, CarDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(carService, times(1)).isValidToken("validToken");

        verify(carService, times(1)).deleteCar(4L);
    }

    @Test
    void testDeleteCarUnauthorized() {
        when(carService.isValidToken("invalidToken")).thenReturn(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalidToken");
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CarDTO> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CarDTO> response = restTemplate.exchange(buildCarURI("/api/cars/4"), HttpMethod.DELETE, requestEntity, CarDTO.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(carService, times(1)).isValidToken("invalidToken");

        verify(carService, never()).deleteCar(anyLong());
    }

    private void addHeaderInterceptorsForBearer(String token) {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    String authHeader = "Bearer " + token;
                    request.getHeaders().add("Authorization", authHeader);
                    return execution.execute(request, body);
                }));
    }

    private URI buildCarURI(String uri) {
        return createURIBuilder().host("localhost").port(port)
                .scheme("http").path(uri)
                .build();
    }

    public static UriBuilder createURIBuilder() {
        UriBuilderFactory factory = new DefaultUriBuilderFactory();
        return factory.builder();
    }

}

