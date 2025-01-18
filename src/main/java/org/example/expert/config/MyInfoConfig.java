package org.example.expert.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

@Getter
@Configuration
@PropertySource(value = "classpath:myInfo.yml", factory = YamlPropertySourceFactory.class)
public class MyInfoConfig {

	@Value("${db.user}")
	private String user;

	@Value("${db.name}")
	private String name;

	@Value("${db.pw}")
	private String pw;

	@Value("${jwt.secret.key}")
	private String secretKey;
}
