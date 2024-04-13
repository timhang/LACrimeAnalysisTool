package com.dsci551.LACrimeAnalysisTool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class LaCrimeAnalysisToolApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(LaCrimeAnalysisToolApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(LaCrimeAnalysisToolApplication.class);
	}
}
