package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

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

	@Value("${cigs.runaquisition:true}")
	private boolean runAquisition = true;

	private Long timeStepSize = 1000L;

	private List<StateListener> listeners = new LinkedList<>();

	private List<Consumer<DataPoint>> newDataPointListeners = new LinkedList<>();

	private DataLoggingService loggingService;

	private ExecutorService executor;

	private LogRunnable runnable;

	private LogSession currentSession;

	private DataPoint lastDataPoint;

	private boolean shouldLog = false;

	private Object dataPointLock = new Object();

	@Autowired
	public DataAquisitionService(DataLoggingService loggingService) {
		super();
		this.loggingService = loggingService;
		this.executor = Executors.newSingleThreadExecutor();
		runnable = new LogRunnable();

	}

	@PostConstruct
	protected void postConstruct() {
		if (runAquisition == true) {
			executor.execute(runnable);
		} else {
			log.warn(
					"Auqisition service is not running! (Setting: cigs.runaquisition=false");
		}
	}

	public void addStateListener(StateListener listener) {
		listeners.add(listener);
		listener.newState(runnable.isRunning(),
				runnable.isRunning() ? currentSession : null);
	}

	public void removeListener(StateListener listener) {
		listeners.remove(listener);
	}

	public void startLogging() throws IOException {
		if (currentSession != null) {
			log.debug("Already logging");
			throw new IllegalStateException(
					"Tried to start logging, but were already logging to a session.");
		}
		log.debug("Starting logging...");
		prepareForNewSession();
	}

	private void prepareForNewSession() throws IOException {
		currentSession = loggingService.createNewSession();
		shouldLog = true;
		fireNewStateEvent(true, currentSession);
	}

	public void stopLogging() {
		shouldLog = false;
	}

	private void logDataPoint() {
		currentSession = loggingService.addDataPoint(lastDataPoint,
				currentSession);
	}

	private final class LogRunnable implements Runnable {

		private boolean running = false;

		private int consecutiveErrors = 0;

		private long deltaTime;

		@Override
		public void run() {
			running = true;

			try {
				Thread.sleep(10000);
				while (running) {
					long lastTime = System.currentTimeMillis();
					try {

						getNewDataPoint();

						if (shouldLog) {
							try {
								logDataPoint();
							} catch (IllegalArgumentException e) {
								// Log session doesnt exist
								stopLogging();
							}

						} else {
							if (currentSession != null) {
								fireNewStateEvent(false, null);
								currentSession = null;
							}
						}
						consecutiveErrors = 0;
						deltaTime = timeStepSize;
					} catch (Exception e) {
						if (e instanceof InterruptedException) {
							throw e;
						}

						log.debug("Error in data aquisition.", e);
						consecutiveErrors++;
						log.debug(String.format(
								"This error is the %d consecutive error.",
								consecutiveErrors));

						if (consecutiveErrors == 20) {
							log.warn(
									"More than 20 consecutive errors. Setting update interval to 1 min");
							deltaTime = 60000L;
						}
						if (consecutiveErrors == 10) {
							log.warn(
									"To many consecutive errors. Throtteling service to 10s. Current session will not be stopped by this.");
							deltaTime = 10000L;
						}

					}

					long currentTime = System.currentTimeMillis();
					long div = currentTime - lastTime;
					if (div < deltaTime) {
						Thread.sleep(deltaTime - div);
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

	public void testGetDataPoint() {
		getNewDataPoint();
		if (shouldLog) {
			logDataPoint();
		}
	}

	@Autowired
	private RestTemplate template;

	private void getNewDataPoint() {
		synchronized (dataPointLock) {
			lastDataPoint = template.getForObject(cigsApi, DataPoint.class);
			lastDataPoint.setDateTime(LocalDateTime.now());
		}
		newDataPointListeners
				.forEach(listener -> listener.accept(lastDataPoint));
	}

	public LogSession getActiveSession() {
		return currentSession;
	}

	public interface StateListener {
		public void newState(Boolean currentState, LogSession currentSession);
	}

	private void fireNewStateEvent(Boolean currentState,
			LogSession currentSession) {
		listeners.forEach(
				listener -> listener.newState(currentState, currentSession));
	}

	public Boolean isLogging() {
		return shouldLog;
	}

	public DataPoint getLastDataPoint() {
		synchronized (dataPointLock) {
			return lastDataPoint;
		}
	}

	public void registerDataPointListener(Consumer<DataPoint> consumer) {
		newDataPointListeners.add(consumer);
	}

	public void removeDataPointListener(Consumer<DataPoint> consumer) {
		newDataPointListeners.remove(consumer);
	}

	public void setTimeStepSize(Long timeStepSize) {
		this.timeStepSize = timeStepSize;
	}
	public Long getTimeStepSize() {
		return timeStepSize;
	}

}
