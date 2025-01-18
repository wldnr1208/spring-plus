package org.example.expert.config;

import java.util.Properties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import java.io.IOException;

public class YamlPropertySourceFactory implements PropertySourceFactory {
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
		throws IOException {
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(encodedResource.getResource());

		Properties properties = factory.getObject();

		return new PropertiesPropertySource(
			encodedResource.getResource().getFilename(), properties
		);
	}
}