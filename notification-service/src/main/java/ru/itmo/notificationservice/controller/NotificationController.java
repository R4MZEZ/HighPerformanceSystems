package ru.itmo.notificationservice.controller;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.notificationservice.websocket.Handler;

@RequiredArgsConstructor
@RestController
@EnableKafka
public class NotificationController {

	private final Handler handler;

	private static final String KAFKA_TOPIC_NAME = "notification";

	//	@PostMapping("/messages/send")
	@KafkaListener(topics = KAFKA_TOPIC_NAME)
	public void send(@Payload String to,
					@Header(KafkaHeaders.RECEIVED_KEY) String from) throws IOException {
		handler.sendMatchNotification(to, from);
	}

}
