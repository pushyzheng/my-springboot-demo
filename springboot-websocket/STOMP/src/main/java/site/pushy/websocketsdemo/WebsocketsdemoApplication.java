package site.pushy.websocketsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class WebsocketsdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketsdemoApplication.class, args);
	}

}
