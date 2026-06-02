package com.hem.EduCore;

import com.hem.EduCore.config.EnvConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EduCoreApplication {

	static {
		new EnvConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(EduCoreApplication.class, args);
	}

}
