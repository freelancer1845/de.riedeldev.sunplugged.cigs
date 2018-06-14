package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataAquisitionService {

	@Value("${cigs.api}")
	private String cigsApi;

	private DataLoggingService loggingService;

	private ExecutorService executor;

	private LogRunnable runnable;

	private LogSession currentSession;

	@Autowired
	public DataAquisitionService(DataLoggingService loggingService) {
		super();
		this.loggingService = loggingService;
		this.executor = Executors.newSingleThreadExecutor();
		runnable = new LogRunnable();
	}

	public void startLogging() {
		if (runnable.isRunning() == false) {
			log.debug("Already logging. Starting new session!");
			runnable.stop();
		}

		log.debug("Starting logging...");
		prepareForNewSession();
		executor.execute(runnable);
	}

	private void prepareForNewSession() {
		currentSession = loggingService.createNewSession();
	}

	public void stopLogging() {
		runnable.stop();
	}

	private void logDataPoint() {
		RestTemplate template = new RestTemplate();
		DataPoint point = template.getForObject(cigsApi, DataPoint.class);
		currentSession = loggingService.addDataPoint(point, currentSession);
	}

	private final class LogRunnable implements Runnable {

		private boolean running = false;

		@Override
		public void run() {
			running = true;

			try {
				while (running) {
					long lastTime = System.currentTimeMillis();
					logDataPoint();
					long currentTime = System.currentTimeMillis();
					long div = currentTime - lastTime;
					if (div < 1000) {
						Thread.sleep(1000 - div);
					}

				}
			} catch (InterruptedException e) {
				log.debug("Log Runnable interrupted!", e);
			}
			running = false;
		}

		public void stop() {
			log.debug("LogRunnable stopped.");
			this.running = false;
		}

		public boolean isRunning() {
			return this.running;
		}

	}

	public LogSession getActiveSession() {
		return currentSession;
	}

}
