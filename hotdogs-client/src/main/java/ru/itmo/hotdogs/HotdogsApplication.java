package ru.itmo.hotdogs;

import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableFeignClients
public class HotdogsApplication{

	public static void main(String[] args) {
		SpringApplication.run(HotdogsApplication.class, args);
	}

	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}


}
