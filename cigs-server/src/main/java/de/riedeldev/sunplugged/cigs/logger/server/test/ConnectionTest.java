package de.riedeldev.sunplugged.cigs.logger.server.test;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.cigs.logger.server.main.MainApplication;

@Service
public class ConnectionTest {
	
	public static void main(String[] args) throws InterruptedException {
		update();
	}
	
	public static void update() throws InterruptedException {
		System.out.println("Started");
		while(true) {
			for (int i = 0; i < 15; i++) {
				String value = BR_ASP_Request.readPv(String.format("termo:TempPID[%d].Temp", i), "192.168.1.80");
				System.out.println("Temp " + i + ": " + value);
			}
		}
		
	}
	
	
}
