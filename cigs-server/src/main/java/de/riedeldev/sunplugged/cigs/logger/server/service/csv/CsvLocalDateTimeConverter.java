package de.riedeldev.sunplugged.cigs.logger.server.service.csv;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class CsvLocalDateTimeConverter extends AbstractBeanField<LocalDateTime> {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	protected Object convert(String text) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
		return LocalDateTime.parse(text, formatter);
	}

	@Override
	protected String convertToWrite(Object value) throws CsvDataTypeMismatchException {
		return ((LocalDateTime) value).format(formatter);
	}

}
