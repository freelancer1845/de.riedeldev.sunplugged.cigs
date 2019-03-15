package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.DataPointUtils.DataPointField;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogFileService {

	@Value("${logfileservice.path}")
	private String path = "data";

	private static final String FILE_PREFIX = "cigs-";

	private static final String ENDING = ".csv";

	private static final char NEWLINE = (char) 10;

	private static final char DELIMITER = (char) 44;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private BlockingQueue<Pair<LogSession, DataPoint>> queue = new LinkedBlockingDeque<>();

	private DataPointsCache cache = new DataPointsCache();

	private volatile LogSession activeSession = null;

	private List<DataPoint> activeDataPoints = new ArrayList<>();

	private Lock activeAccessLock = new ReentrantLock();

	@PreDestroy
	protected void preDestroy() {
		executor.shutdownNow();
	}

	public LogFileService() {
		// check if directory exists

		File file = new File(path);
		if (file.exists() == false) {
			file.mkdirs();
			file.mkdir();
		}
		if (file.isDirectory() == false) {
			throw new IllegalStateException("Path for logfiles was not a directory");
		}

		executor.submit(() -> {
			try {
				StringBuilder buffer = new StringBuilder();

				while (true) {
					if (Thread.interrupted() == true) {
						break;
					}
					long timeSinceLastFlush = System.currentTimeMillis();
					while ((System.currentTimeMillis() - timeSinceLastFlush) < 3000) {

						Pair<LogSession, DataPoint> nextPair = queue.poll(500, TimeUnit.MILLISECONDS);
						if (nextPair == null) {
							continue;
						}
						if (activeSession != null && activeSession.getId() != nextPair.getFirst().getId()) {
							try {
								activeAccessLock.lock();
								flushBuffer(activeSession, buffer);
								activeDataPoints.clear();
								timeSinceLastFlush = System.currentTimeMillis();
							} finally {
								activeAccessLock.unlock();
							}

						}
						buffer.append(dataPointToCsvLine(nextPair.getSecond()));

						try {
							activeAccessLock.lock();
							activeSession = nextPair.getFirst();
							activeDataPoints.add(nextPair.getSecond());
						} finally {
							activeAccessLock.unlock();
						}

					}
					if (activeSession != null) {
						try {
							activeAccessLock.lock();
							flushBuffer(activeSession, buffer);
						} finally {
							activeAccessLock.unlock();
						}
						timeSinceLastFlush = System.currentTimeMillis();
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	public InputStream getInputStreamToLogFile(LogSession session) throws FileNotFoundException {
		FileInputStream stream = new FileInputStream(new File(session.getLogFilePath())) {

			private boolean firstByteRead = false;

			private boolean hasLock = false;

			@Override
			public int read() throws IOException {
				if (firstByteRead == false) {
					firstByteRead = true;
					if (activeSession != null && session.getId() == activeSession.getId()) {
						activeAccessLock.lock();
						hasLock = true;
					}
				}
				return super.read();
			}

			@Override
			public int read(byte[] b) throws IOException {
				if (firstByteRead == false) {
					firstByteRead = true;
					if (activeSession != null && session.getId() == activeSession.getId()) {
						activeAccessLock.lock();
						hasLock = true;
					}
				}
				return super.read(b);
			}

			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				if (firstByteRead == false) {
					firstByteRead = true;
					if (activeSession != null && session.getId() == activeSession.getId()) {
						activeAccessLock.lock();
						hasLock = true;
					}
				}
				return super.read(b, off, len);
			}

			@Override
			public void close() throws IOException {
				if (hasLock) {
					activeAccessLock.unlock();
				}

				super.close();
			}
		};

		return stream;
	}

	private void flushBuffer(LogSession currentSession, StringBuilder buffer) throws IOException {
		if (buffer.length() < 1) {
			return;
		}
		File logFile = new File(currentSession.getLogFilePath());
		checkFileAccess(logFile);
		try (BufferedWriter br = new BufferedWriter(new FileWriter(logFile, true))) {
			br.write(buffer.toString());
			buffer.delete(0, buffer.length());
		} catch (IOException e) {
			log.error("Failed to flush buffer..", e);
		}
	}

	public boolean checkIfLogFileExists(LogSession logSession) {
		if (logSession.getLogFilePath() == null || logSession.getLogFilePath().isEmpty()) {
			return false;
		}
		File logfile = new File(logSession.getLogFilePath());
		if (logfile.isDirectory()) {
			throw new IllegalStateException("LogSession has a directory as logfile path attached!");
		}
		return logfile.exists();
	}

	public LogSession createLogFileForLogSession(LogSession logSession) throws IOException {
		logSession.setLogFilePath(generateUniquePath(logSession));

		File file = new File(logSession.getLogFilePath());

		file.createNewFile();

		createHeaderForLogFile(file);

		return logSession;
	}

	private void createHeaderForLogFile(File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			StringBuilder builder = new StringBuilder();

			List<DataPointField> fields = DataPointField.getAllDataPointFieldsSorted();
			fields.stream().sequential()
					.forEach(field -> builder.append(String.format("\"%s\"" + DELIMITER, field.name)));

			builder.deleteCharAt(builder.length() - 1);
			builder.append(NEWLINE);
			writer.write(builder.toString());
		}
	}

	private String generateUniquePath(LogSession logSession) {
		StringBuilder builder = new StringBuilder();

		builder.append(path);
		builder.append("/");
		builder.append(FILE_PREFIX);
		builder.append(logSession.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")));
		String filePathWithoutEnding = builder.toString();

		File file;
		String filePath;
		int sameNameIndex = 0;
		do {
			if (sameNameIndex > 0) {
				filePath = filePathWithoutEnding + "-" + sameNameIndex + ENDING;
			} else {
				filePath = filePathWithoutEnding + ENDING;
			}
			file = new File(filePath);
			sameNameIndex++;
		} while (file.exists());

		return file.getAbsolutePath();
	}

	public void saveDataPoint(DataPoint point, LogSession logSession) {

		queue.offer(Pair.of(logSession, point));
		// executor.submit(() -> writeDataPointToFile(point, logSession));
	}

	public List<DataPoint> getDataPoints(LogSession session) throws IOException {
		List<DataPoint> points = null;
		if (activeSession != null) {
			if (activeSession.getId() == session.getId()) {
				try {
					activeAccessLock.lock();
					points = new ArrayList<>(activeDataPoints);
				} finally {
					activeAccessLock.unlock();
				}
			}
		}

		if (points == null) {
			try {
				activeAccessLock.lock();
				points = cache.getDataPoints(session);
			} finally {
				activeAccessLock.unlock();
			}
		}

		if (points != null) {
			return points;
		} else {
			throw new IllegalStateException("Failed to load datapoints.");
		}

	}

	public void deleteDataFileForLogSession(LogSession session) {
		File file = new File(session.getLogFilePath());
		file.delete();
	}

	private void checkFileAccess(File file) throws IOException {
		if (file.isDirectory()) {
			throw new IOException("Trying to write to a directory.");
		}
		if (file.isFile() == false) {
			throw new IOException("File does not exsist");
		}
		if (file.canWrite() == false) {
			throw new IOException("No write permission to file");
		}
		if (file.getFreeSpace() < 50000000L) {
			throw new IOException("Not enough storage available. < 50MB");
		}
	}

	private String dataPointToCsvLine(DataPoint point) {
		List<DataPointField> fields = DataPointField.getAllDataPointFieldsSorted();

		StringBuilder builder = new StringBuilder();

		fields.stream().map(field -> fieldFromDataPointToString(field, point))
				.forEach(value -> builder.append(String.format("\"%s\"" + DELIMITER, value)));
		builder.deleteCharAt(builder.length() - 1);
		builder.append(NEWLINE);
		return builder.toString();
	}

	private String fieldFromDataPointToString(DataPointField field, DataPoint dataPoint) {
		String value;
		try {
			if (field.field.getType() == LocalDateTime.class) {

				LocalDateTime time = (LocalDateTime) PropertyUtils.getProperty(dataPoint, field.field.getName());
				value = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} else if (field.field.getType() == Double.class) {
				Double number = (Double) PropertyUtils.getProperty(dataPoint, field.field.getName());
				if (number == null) {
					value = "N\\A";
				} else if (number.isNaN()) {
					value = "NaN";
				} else {
					DecimalFormat format;
					if (number > 0.001 && number < 10000) {
						format = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));
					} else {
						format = new DecimalFormat("#.###E00", DecimalFormatSymbols.getInstance(Locale.US));
					}
					value = format.format(number.doubleValue());
				}

			} else {
				Object object = PropertyUtils.getProperty(dataPoint, field.field.getName());
				if (object == null) {
					value = "N\\A";
				} else {
					value = object.toString();
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalArgumentException("Faild to read field", e);
		}

		return value;
	}

}
