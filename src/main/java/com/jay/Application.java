package com.jay;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication

public class Application {

  public static void main(String[] args) throws Exception {
    SpringApplication application = new SpringApplication(Application.class);
    application.run(args);
  }

}
