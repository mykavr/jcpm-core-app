package com.theroom307.jcpm.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "JCPM Core API", version = "1"))
public class JcpmCoreApp {

	public static void main(String[] args) {
		SpringApplication.run(JcpmCoreApp.class, args);
	}

}
