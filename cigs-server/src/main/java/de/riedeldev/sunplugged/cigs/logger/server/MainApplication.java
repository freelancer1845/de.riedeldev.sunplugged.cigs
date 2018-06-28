package de.riedeldev.sunplugged.cigs.logger.server;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;

@SpringBootApplication
@EnableScheduling
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(DataLoggingService service) {
		return args -> {
			for (int j = 0; j < 1; j++) {
				LogSession session = service.createNewSession();
				Random random = new Random();
				System.out.println("Session Created");
				for (int i = 0; i < 3600 * 2; i++) {
					DataPoint dataPoint = new DataPoint();
					dataPoint.setDateTime(LocalDateTime.now()
							.plusSeconds(i));

					dataPoint.setSubstrateTempOne(Math.sin(i * Math.PI / 3600.0 * 2.0));
					dataPoint.setSubstrateTempTwo(Math.sin(i * Math.PI / 3600.0 * 2.0));
					dataPoint.setSubstrateTempThree(Math.sin(i * Math.PI / 3600.0 * 2.0));
					dataPoint.setSubstrateTempFour(Math.sin(i * Math.PI / 3600.0 * 2.0));
					service.addDataPoint(dataPoint, session);
				}
			}
		};

	}
}
