package in.aprilfish.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class SimpleSpringApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(SimpleSpringApplication.class, args);
	}

}
