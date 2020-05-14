package com.syncrotess.openfriday.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan ({"com.syncrotess.openfriday*"})
@EntityScan ({"com.syncrotess.openfriday*"})
public class Application {

    public static void main (String[] args) {
        SpringApplication.run (Application.class, args);
    }
}
