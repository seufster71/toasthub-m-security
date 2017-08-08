package org.toasthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"org.toasthub"})
public class ToasthubMSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToasthubMSecurityApplication.class, args);
	}
}
