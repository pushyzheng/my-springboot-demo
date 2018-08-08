package site.pushy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "site.pushy")
public class ServiceImplApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceImplApplication.class, args);
	}
}
