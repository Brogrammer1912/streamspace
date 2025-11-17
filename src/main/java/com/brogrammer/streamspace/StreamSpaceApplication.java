package com.brogrammer.streamspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class StreamSpaceApplication {

	public static void main(String[] args) {

		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			System.setProperty("java.net.preferIPv4Stack", "true");
		}

		SpringApplication.run(StreamSpaceApplication.class, args);
	}

}
