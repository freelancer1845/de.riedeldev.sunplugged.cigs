package de.riedeldev.sunplugged.cigs.logger.server.model;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DataPointUtils {

	public static final class DataPointField {

		public final int csvPosition;
		public final String name;
		public final Field field;

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

		public static List<DataPointField> getAllDataPointFieldsSorted() {
			if (fields == null) {
				fields = Arrays.stream(DataPoint.class.getDeclaredFields())
						.filter(field -> field
								.getAnnotation(LogSettings.class) != null)
						.map(field -> new DataPointField(field))
						.collect(Collectors.toList());

				fields.sort((field1, field2) -> Integer
						.compare(field1.csvPosition, field2.csvPosition));
			}
			return Collections.unmodifiableList(fields);
		}

		public static DataPointField getDateField() {
			return getAllDataPointFieldsSorted().stream().filter(
					field -> field.field.getType() == LocalDateTime.class)
					.findFirst().orElse(null);
		}

	}
}
