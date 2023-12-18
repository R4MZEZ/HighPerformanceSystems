package ru.itmo.ownerservice;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class OwnerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OwnerServiceApplication.class, args);
	}

	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}

}
