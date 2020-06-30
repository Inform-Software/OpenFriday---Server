package com.syncrotess.openfriday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan ({"com.syncrotess.openfriday.*"})
//@EntityScan ({"com.syncrotess.openfriday.*"})
public class Application {

    public static void main (String[] args) {
        SpringApplication.run (Application.class, args);
    }
}
