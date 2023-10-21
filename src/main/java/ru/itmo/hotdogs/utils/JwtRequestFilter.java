package ru.itmo.hotdogs.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.itmo.hotdogs.utils.JwtTokenUtils;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	private final JwtTokenUtils jwtTokenUtils;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String login = null;
		String jwt = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")){
			jwt = authHeader.substring(7);
			try {
				login = jwtTokenUtils.getUsername(jwt);
			}catch (ExpiredJwtException e){
				System.out.println("Токен протух");
			}catch (SignatureException e){
				System.out.println("Подпись палёная");
			}
		}

		if (login != null){
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				login,
				null,
				jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(
					Collectors.toList())
			);
			SecurityContextHolder.getContext().setAuthentication(token);
		}
		filterChain.doFilter(request,response);
	}
}
