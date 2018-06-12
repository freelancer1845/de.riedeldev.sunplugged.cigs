package de.riedeldev.sunplugged.cigs.logger.server;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(DataLoggingService service) {
        return args -> {
            for (int j = 0; j < 5; j++) {
                LogSession session = service.createNewSession();
                System.out.println("Session Created");
                for (int i = 0; i < 100; i++) {
                    DataPoint dataPoint = new DataPoint();
                    dataPoint.setDateTime(LocalDateTime.now());
                    dataPoint.setSubstrateTempOne(i * 0.5);
                    dataPoint.setSubstrateTempTwo(i * 0.5);
                    dataPoint.setSubstrateTempThree(i * 0.5);
                    dataPoint.setSubstrateTempFour(i * 0.5);
                    service.addDataPoint(dataPoint, session.getId());
                }
            }
        };

    }
}
