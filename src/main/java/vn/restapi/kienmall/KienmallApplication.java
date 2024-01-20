package vn.restapi.kienmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication()
@EnableConfigurationProperties
@PropertySource(value = "file:config/application.properties", encoding="UTF-8")
public class KienmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(KienmallApplication.class, args);
	}

}
