package com.opcopilot.queryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class QueryserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryserviceApplication.class, args);
	}

}
