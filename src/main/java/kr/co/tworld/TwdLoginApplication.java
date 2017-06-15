package kr.co.tworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TwdLoginApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwdLoginApplication.class, args);
	}
}
