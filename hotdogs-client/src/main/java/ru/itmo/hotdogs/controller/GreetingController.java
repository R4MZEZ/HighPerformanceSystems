package ru.itmo.hotdogs.controller;

import org.springframework.web.bind.annotation.RequestMapping;

public interface GreetingController {
	@RequestMapping("/greeting")
	String greeting();
}
