package web.app.webflux_moldunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class WebfluxMoldunityApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Chisinau"));
		SpringApplication.run(WebfluxMoldunityApplication.class, args);
	}
}
