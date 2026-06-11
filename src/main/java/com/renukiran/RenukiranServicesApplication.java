package com.renukiran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RenukiranServicesApplication {

	public static void main(String[] args) {
		java.util.TimeZone.setDefault(
				java.util.TimeZone.getTimeZone("UTC")
		);
		SpringApplication.run(RenukiranServicesApplication.class, args);
	}

}
