package de.riedeldev.sunplugged.cigs.logger.server.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

import de.riedeldev.sunplugged.cigs.logger.server.service.csv.CsvLocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "DATA_POINTS")
@Data
@JsonIgnoreProperties
@EqualsAndHashCode(of = { "id", "dateTime" })
public class DataPoint {

	@Id
	@GeneratedValue
	private Long id;

	@CsvCustomBindByName(column = "Time", converter = CsvLocalDateTimeConverter.class)
	private LocalDateTime dateTime;

	@ManyToOne(targetEntity = LogSession.class, optional = false)
	private LogSession session;

	@CreateChart
	@CsvBindByName
	private Double substrateTempOne;

	@CreateChart
	@CsvBindByName
	private Double substrateTempTwo;

	@CreateChart
	@CsvBindByName
	private Double substrateTempThree;

	@CreateChart
	@CsvBindByName
	private Double substrateTempFour;

}
