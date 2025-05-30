package com.amz.scm;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Scm2apiApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {

        SpringApplication.run(Scm2apiApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Override
    public void run(String... args) throws Exception {
       
        String encodedPasswordUserWithId2 = this.passwordEncoder.encode("adminaayush122");
        System.out.println(encodedPasswordUserWithId2);
    }

    


}
