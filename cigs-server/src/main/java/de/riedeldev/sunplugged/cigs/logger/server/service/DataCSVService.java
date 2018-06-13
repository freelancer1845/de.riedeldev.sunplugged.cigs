package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.StringWriter;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.repository.DataPointRepository;

@Service
public class DataCSVService {

	private DataPointRepository dataRepo;

	@Autowired
	public DataCSVService(DataPointRepository dataRepo) {
		this.dataRepo = dataRepo;
	}

	@Transactional
	public String sessionToCsv(LogSession session) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		StringWriter writer = new StringWriter();

		StatefulBeanToCsv<DataPoint> csvWriter = new StatefulBeanToCsvBuilder<DataPoint>(writer).build();

		csvWriter.write(dataRepo.findAllBySession(session).collect(Collectors.toList()));

		return writer.toString();
	}

}
