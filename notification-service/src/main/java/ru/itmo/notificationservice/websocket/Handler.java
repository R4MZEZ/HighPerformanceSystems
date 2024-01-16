package ru.itmo.notificationservice.websocket;

import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.itmo.notificationservice.utils.JwtUtils;

@Component
@RequiredArgsConstructor
public class Handler implements WebSocketHandler {

	private final Map<String, WebSocketSession> sessions = new HashMap<>();
	private final Map<String, String> session_ids = new HashMap<>();

	private final Log log = LogFactory.getLog(getClass());

	private final JwtUtils jwtUtils;

	public void sendMatchNotification(String receiver, String sender) throws IOException {
		if (sessions.containsKey(receiver))
			sessions.get(receiver).sendMessage(new TextMessage(String.format("It's a match! With %s!", sender)));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String header = session.getHandshakeHeaders().getFirst("Authorization");
		try {
			String username = jwtUtils.getUsernameFromHeader(header);
			if (!sessions.containsKey(username))
				session_ids.put(session.getId(), username);
			sessions.put(username, session);
		} catch (ExpiredJwtException | IllegalArgumentException e) {
			session.close(CloseStatus.POLICY_VIOLATION);
		}

	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
		throws Exception {
		log.debug(String.format("Session %s sent message: '%s'", session.getId(), message.getPayload()));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception)
		throws Exception {
		log.error(exception.getMessage());

	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
		throws Exception {
		if (session_ids.containsKey(session.getId())) {
			sessions.remove(session_ids.get(session.getId()));
			session_ids.remove(session.getId());
		}
	}

	@Override
	public boolean supportsPartialMessages() {
		return false;
	}
}
