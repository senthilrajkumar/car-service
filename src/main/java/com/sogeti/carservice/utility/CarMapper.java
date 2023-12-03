package com.sogeti.carservice.utility;

import com.sogeti.carservice.dto.CarDTO;
import com.sogeti.carservice.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarDTO carToCarDTO(Car car);
    Car carDTOToCar(CarDTO carDTO);
}
