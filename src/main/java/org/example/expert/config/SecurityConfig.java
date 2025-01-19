package org.example.expert.config;

import org.example.expert.config.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final org.example.expert.config.PasswordEncoder customPasswordEncoder;
	private final CustomUserDetailsService userDetailsService;

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
			.userDetailsService(userDetailsService)
			.passwordEncoder(new org.springframework.security.crypto.password.PasswordEncoder() {
				@Override
				public String encode(CharSequence rawPassword) {
					return customPasswordEncoder.encode(rawPassword.toString());
				}

				@Override
				public boolean matches(CharSequence rawPassword, String encodedPassword) {
					return customPasswordEncoder.matches(rawPassword.toString(), encodedPassword);
				}
			})
			.and()
			.build();
	}


	// 기존 SecurityFilterChain 설정 유지
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/auth/**",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/swagger-ui.html",
					"/swagger-resources/**",
					"/webjars/**",
					"/h2-console/**",
					"/").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}