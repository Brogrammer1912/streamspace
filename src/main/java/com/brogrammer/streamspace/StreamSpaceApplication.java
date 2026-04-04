package com.brogrammer.streamspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
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
