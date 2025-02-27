package com.flowx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // activate scheduled tasks
public class FlowxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowxServerApplication.class, args);
	}

}
