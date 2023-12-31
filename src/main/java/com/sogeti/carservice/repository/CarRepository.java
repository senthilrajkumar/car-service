package com.sogeti.carservice.repository;

import com.sogeti.carservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
}
