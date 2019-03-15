package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.DataPointUtils.DataPointField;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataPointsCache {

	private static final int CACHE_SIZE = 6;

	private Map<String, CachedDataPointFile> cache = new LinkedHashMap<>();

	public List<DataPoint> getDataPoints(LogSession logSession)
			throws FileNotFoundException, IOException {

		String path = logSession.getLogFilePath();
		if (cache.containsKey(path)) {

			File file = new File(path);

			if (file.exists() == false) {
				cache.remove(path);
				throw new IllegalStateException(
						"LogFile does not exist. " + path);
			}

			long lastmodified = file.lastModified();

			CachedDataPointFile cachedDataPointFile = cache.get(path);

			if (cachedDataPointFile.getLastModified() < lastmodified) {
				loadFile(logSession);
			} else {
				System.out.println("Loaded from cache");
			}

		} else {
			loadFile(logSession);
		}

		return cache.get(path).getCachedDataPoints();
	}

	private void loadFile(LogSession logSession)
			throws FileNotFoundException, IOException {
		File file = new File(logSession.getLogFilePath());

		if (cache.size() > CACHE_SIZE) {
			cache.remove(cache.entrySet().iterator().next().getKey());
		}
		try (FileReader reader = new FileReader(file);
				BufferedReader br = new BufferedReader(reader)) {
			// Skip header
			br.readLine();
			CachedDataPointFile cachedFile;
			// Skip already read lines
			if (cache.containsKey(logSession.getLogFilePath())) {
				cachedFile = cache.get(logSession.getLogFilePath());
			} else {
				cachedFile = new CachedDataPointFile();
			}

			// Skip already read entries
			for (int i = 0;i < cachedFile.getCachedDataPoints().size(); i++) {
				br.readLine();
			}

			List<DataPointField> fields = DataPointField
					.getAllDataPointFieldsSorted();
			String line;
			long timebefore = System.currentTimeMillis();

			cachedFile.setLastModified(file.lastModified());
			int before = cachedFile.getCachedDataPoints().size();
			while ((line = br.readLine()) != null) {
				DataPoint point = new DataPoint();
				String[] entries = line.split(",");
				for (int i = 0; i < entries.length; i++) {
					final int filePosition = i;
					DataPointField correspondingField = fields.stream().filter(
							field -> field.csvPosition == (filePosition + 1))
							.findFirst().orElse(null);
					if (correspondingField == null) {
						continue;
					}
					String entry = entries[i].replaceAll("\"", "");

					putStringIntoDataPoint(correspondingField, entry, point);
				}
				cachedFile.addPoint(point);
			}
			System.out.println("time Needed: "
					+ (System.currentTimeMillis() - timebefore));
			System.out.println("Entires added: "
					+ (cachedFile.getCachedDataPoints().size() - before));
			cache.put(logSession.getLogFilePath(), cachedFile);
		} catch (IOException e) {
			log.error("Faild to load data into cache.", e);
			throw e;
		}

	}

	private void putStringIntoDataPoint(DataPointField field, String value,
			DataPoint dataPoint) {
		try {
			if (field.field.getType() == LocalDateTime.class) {

				LocalDateTime time = LocalDateTime.parse(value,
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				PropertyUtils.setProperty(dataPoint, field.field.getName(),
						time);

			} else if (field.field.getType() == Double.class) {
				Double number;
				if (value.equals("N\\A")) {
					number = null;
				} else if (value.equals("NaN")) {
					number = Double.NaN;
				} else {
					number = Double.valueOf(value);
				}

				PropertyUtils.setProperty(dataPoint, field.field.getName(),
						number);

			} else if (field.field.getType() == Integer.class) {
				if (value.equals("N\\A")) {
					PropertyUtils.setProperty(dataPoint, field.field.getName(),
							-1);
				} else {
					Integer number = Integer.valueOf(value);
					PropertyUtils.setProperty(dataPoint, field.field.getName(),
							number);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | NumberFormatException e) {
			throw new IllegalArgumentException("Faild to read field", e);
		}

	}

	@Getter
	@Setter
	private final class CachedDataPointFile {

		private long lastModified;

		private List<DataPoint> cachedDataPoints = new LinkedList<>();

		public void addPoint(DataPoint point) {
			cachedDataPoints.add(point);
		}

	}

}
