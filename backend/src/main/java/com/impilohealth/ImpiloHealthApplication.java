package com.impilohealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ImpiloHealthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImpiloHealthApplication.class, args);
    }
}
