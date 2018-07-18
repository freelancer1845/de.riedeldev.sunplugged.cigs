package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSettings;
import de.riedeldev.sunplugged.cigs.logger.server.repository.DataPointRepository;

@Service
public class DataCSVService {

	private static final char NEWLINE = (char) 10;

	private static final char DELIMITER = (char) 44;

	private DataPointRepository dataRepo;

	@Autowired
	public DataCSVService(DataPointRepository dataRepo) {
		this.dataRepo = dataRepo;
	}

	public String sessionToCsv(LogSession session) {
		return sessionToString(session);
	}

	private String sessionToString(LogSession session) {
		StringBuilder builder = new StringBuilder();
		builder.append(getHeader());
		builder.append(NEWLINE);

		List<DataPoint> dataPoints = getDataPoints(session);

		dataPoints.stream().sequential().map(this::dataPointToLine)
				.forEach(line -> builder.append(line + NEWLINE));

		return builder.toString();
	}

	private String getHeader() {
		StringBuilder builder = new StringBuilder();

		List<DataPointField> fields = DataPointField
				.getAllDataPointFieldsSorted();
		fields.stream().sequential().forEach(field -> builder
				.append(String.format("\"%s\"" + DELIMITER, field.name)));

		return builder.toString();
	}

	@Transactional(readOnly = true)
	private List<DataPoint> getDataPoints(LogSession session) {
		return dataRepo.findAllBySession(session);
	}

	private String dataPointToLine(DataPoint dataPoint) {
		List<DataPointField> fields = DataPointField
				.getAllDataPointFieldsSorted();

		StringBuilder builder = new StringBuilder();

		fields.stream()
				.map(field -> fieldFromDataPointToString(field, dataPoint))
				.forEach(value -> builder
						.append(String.format("\"%s\"" + DELIMITER, value)));

		return builder.toString();
	}

	private String fieldFromDataPointToString(DataPointField field,
			DataPoint dataPoint) {
		String value;
		try {
			if (field.field.getType() == LocalDateTime.class) {

				LocalDateTime time = (LocalDateTime) PropertyUtils
						.getProperty(dataPoint, field.field.getName());
				value = time.format(
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			} else if (field.field.getType() == Double.class) {
				Double number = (Double) PropertyUtils.getProperty(dataPoint,
						field.field.getName());
				if (number == null) {
					value = "N\\A";
				} else if (number.isNaN()) {
					value = "NaN";
				} else {
					NumberFormat format = NumberFormat.getInstance(Locale.US);
					value = format.format(number.doubleValue());
				}

			} else {
				Object object = PropertyUtils.getProperty(dataPoint,
						field.field.getName());
				if (object == null) {
					value = "N\\A";
				} else {
					value = object.toString();
				}
			}
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			throw new IllegalArgumentException("Faild to read field", e);
		}

		return value;
	}

	private static final class DataPointField {

		final int csvPosition;
		final String name;
		final Field field;

		public DataPointField(Field field) {
			LogSettings annotation = field
					.getDeclaredAnnotation(LogSettings.class);
			if (annotation == null) {
				throw new IllegalArgumentException(
						"Field must be annotated by LogSettings");
			}
			this.csvPosition = annotation.csvPosition();
			this.name = annotation.nameToDisplay();
			this.field = field;

		}

		private static List<DataPointField> fields = null;

		private static List<DataPointField> getAllDataPointFieldsSorted() {
			if (fields == null) {
				fields = Arrays.stream(DataPoint.class.getDeclaredFields())
						.filter(field -> field
								.getAnnotation(LogSettings.class) != null)
						.map(field -> new DataPointField(field))
						.collect(Collectors.toList());

				fields.sort((field1, field2) -> Integer
						.compare(field1.csvPosition, field2.csvPosition));
			}
			return fields;
		}

	}

}
