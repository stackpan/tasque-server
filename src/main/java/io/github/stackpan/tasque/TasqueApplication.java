package io.github.stackpan.tasque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "io.github.stackpan.tasque.config.properties")
public class TasqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(TasqueApplication.class, args);
	}

}
