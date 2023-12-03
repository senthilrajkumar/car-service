package com.sogeti.carservice.controller;
import com.sogeti.carservice.dto.CarDTO;
import com.sogeti.carservice.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Car Management", description = "Car Management APIs")
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Receive All Car Details", security = @SecurityRequirement(name = "bearerToken"), description = "Receive All Car Details after authentication using JWT token")
    @GetMapping
    public ResponseEntity<List<CarDTO>> getAllCars(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && carService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(carService.getAllCars());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Create Car In Car Database", security = @SecurityRequirement(name = "bearerToken"), description = "Create Car after authentication using JWT token")
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && carService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(carService.createCar(carDTO));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Get specific car details", security = @SecurityRequirement(name = "bearerToken"), description = "Get car details after authentication using JWT token")
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && carService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(carService.getCarById(id));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Update specific car details", security = @SecurityRequirement(name = "bearerToken"), description = "Update car details after authentication using JWT token")
    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && carService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.OK).body(carService.updateCar(id, carDTO));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Delete specific car", security = @SecurityRequirement(name = "bearerToken"), description = "Delete car after authentication using JWT token")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id, HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null && carService.isValidToken(token)) {
            carService.deleteCar(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String extractToken(HttpServletRequest request) {
        // Extract the token from the Authorization header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length());
        }
        return null;
    }
}
