package org.example.expert.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.security.Keys;

public class JwtDecoder {
	private static final String SECRET_KEY = "jwt 시크릿 키 등록"; // 비밀 키

	public static void main(String[] args) {
		// JWT 토큰 (Bearer 접두어 포함)
		String jwtToken = "로그인 토큰 입력";

		try {
			jwtToken = jwtToken.replace("Bearer ", "");

			byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
			Key key = Keys.hmacShaKeyFor(decodedKey);

			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(jwtToken)
				.getBody();

			System.out.println("Claims: " + claims);
			System.out.println("Nickname: " + claims.get("nickname"));

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}
}