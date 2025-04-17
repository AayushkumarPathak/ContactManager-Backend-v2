package com.amz.scm;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Scm2apiApplication {

    public static void main(String[] args) {

        SpringApplication.run(Scm2apiApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    


}
