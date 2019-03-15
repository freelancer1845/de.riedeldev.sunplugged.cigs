package de.riedeldev.sunplugged.cigs.logger.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import de.riedeldev.sunplugged.cigs.logger.server.service.TestService;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration
@ComponentScan(basePackages = "de.riedeldev.sunplugged.cigs.logger.server")
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	// @Bean
	// public CommandLineRunner loadData(DataLoggingService service, TestService testService) {
	// 	// return args -> {
	// 	// 	for (int j = 0; j < 2; j++) {
	// 	// 		LogSession session = service.createNewSession();
	// 	// 		for (int i = 0; i < 10000; i++) {
	// 	// 			service.addDataPoint(testService.getDataPoint().getBody(), session);
	// 	// 		}
	// 	// 	}
	// 	// };

	// }

}
