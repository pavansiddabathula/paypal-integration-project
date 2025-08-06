package com.hulkhiretech.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;
@Slf4j
/**
 * The main class for the PaypalProviderServiceF1 application.
 * This class is responsible for starting the Spring Boot application.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class PaypalProviderServiceF1Application {

	public static void main(String[] args) {
		SpringApplication.run(PaypalProviderServiceF1Application.class, args);
	
	}

}
