package org.example.expert.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String token = resolveToken(request);

		try {
			if (token != null) {
				Claims claims = jwtUtil.extractClaims(token);
				setAuthentication(claims);
			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Error processing JWT token", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
		}
	}

	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void setAuthentication(Claims claims) {
		String userRole = claims.get("userRole", String.class);
		List<GrantedAuthority> authorities = Collections.singletonList(
			new SimpleGrantedAuthority("ROLE_" + userRole)
		);

		Authentication authentication =
			new UsernamePasswordAuthenticationToken(
				claims.getSubject(), null, authorities);

		SecurityContextHolder.getContext()
			.setAuthentication(authentication);
	}
}