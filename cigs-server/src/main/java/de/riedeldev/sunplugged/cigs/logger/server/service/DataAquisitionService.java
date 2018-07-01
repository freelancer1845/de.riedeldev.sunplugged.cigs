package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

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
		executor.execute(runnable);
	}

	public void addStateListener(StateListener listener) {
		listeners.add(listener);
		listener.newState(runnable.isRunning(), runnable.isRunning() ? currentSession : null);
	}

	public void removeListener(StateListener listener) {
		listeners.remove(listener);
	}

	public void startLogging() {
		if (currentSession != null) {
			log.debug("Already logging");
			throw new IllegalStateException("Tried to start logging, but were already logging to a session.");
		}
		log.debug("Starting logging...");
		prepareForNewSession();
	}

	private void prepareForNewSession() {
		currentSession = loggingService.createNewSession();
		shouldLog = true;
		fireNewStateEvent(true, currentSession);
	}

	public void stopLogging() {
		shouldLog = false;
	}

	private void logDataPoint() {

		currentSession = loggingService.addDataPoint(lastDataPoint, currentSession);
	}

	private final class LogRunnable implements Runnable {

		private boolean running = false;

		@Override
		public void run() {
			running = true;

			try {
				Thread.sleep(5000);
				while (running) {
					long lastTime = System.currentTimeMillis();

					getNewDataPoint();

					if (shouldLog) {
						logDataPoint();
					} else {
						if (currentSession != null) {
							fireNewStateEvent(false, null);
							currentSession = null;
						}
					}

					long currentTime = System.currentTimeMillis();
					long div = currentTime - lastTime;
					if (div < timeStepSize) {
						Thread.sleep(timeStepSize - div);
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

	private void getNewDataPoint() {
		RestTemplate template = new RestTemplate();
		synchronized (dataPointLock) {
			lastDataPoint = template.getForObject(cigsApi, DataPoint.class);
			lastDataPoint.setDateTime(LocalDateTime.now());
		}
		newDataPointListeners.forEach(listener -> listener.accept(lastDataPoint));
	}

	public LogSession getActiveSession() {
		return currentSession;
	}

	public interface StateListener {
		public void newState(Boolean currentState, LogSession currentSession);
	}

	private void fireNewStateEvent(Boolean currentState, LogSession currentSession) {
		listeners.forEach(listener -> listener.newState(currentState, currentSession));
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

}
