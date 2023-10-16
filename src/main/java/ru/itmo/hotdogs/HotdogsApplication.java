package ru.itmo.hotdogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.bedatadriven.jackson.datatype.jts.JtsModule;

@SpringBootApplication
public class HotdogsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotdogsApplication.class, args);
	}

	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}
}
