package com.sogeti.carservice.config;

import com.sogeti.carservice.utility.CarMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public CarMapper carMapper() {
        return Mappers.getMapper(CarMapper.class);
    }
}
