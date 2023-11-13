package ru.itmo.hotdogs;

import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.hotdogs.controller.GreetingController;

@SpringBootApplication
@RestController
public class HotdogsApplication implements GreetingController {

	@Autowired
	@Lazy
	private EurekaClient eurekaClient;

	public static void main(String[] args) {
		SpringApplication.run(HotdogsApplication.class, args);
	}

	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}

	@Override
	public String greeting() {
		return String.format(
			"Hello from '%s'!", "biba");
	}
}
