package de.riedeldev.sunplugged.cigs.logger.server.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	@LogSettings(createChart = false, csvPosition = 1, nameToDisplay = "Zeit")
	private LocalDateTime dateTime;

	@ManyToOne(targetEntity = LogSession.class, optional = false)
	private LogSession session;

	@LogSettings(createChart = true, csvPosition = 2, nameToDisplay = "P Baking (Pa)")
	private Double pressureBakingPa;

	@LogSettings(createChart = true, csvPosition = 3, nameToDisplay = "P Full Range 1 (Pa)")
	private Double pressureFullRangePa_1;

	@LogSettings(createChart = true, csvPosition = 4, nameToDisplay = "P Full Range 2 (Pa)")
	private Double pressureFullRangePa_2;

	@LogSettings(createChart = true, csvPosition = 5, nameToDisplay = "I Magnetron 1 (A)")
	private Integer Magnetron_1_I;

	private Integer Magnetron_2_I;

	private Integer Magnetron_3_I;

	private Integer Magnetron_4_I;

	private Integer Magnetron_5_I;

	private Integer Magnetron_6_I;

	private Integer Magnetron_1_V;

	private Integer Magnetron_2_V;

	private Integer Magnetron_3_V;

	private Integer Magnetron_4_V;

	private Integer Magnetron_5_V;

	private Integer Magnetron_6_V;

	private Integer transferDevice_1_I;

	private Integer transferDevice_1_V;

	private Integer transferDevice_2_I;

	private Integer transferDevice_2_V;

	private Integer transferDevice_3_I;

	private Integer transferDevice_3_V;

	private Double substrateTemperature_1;

	private Double substrateTemperature_2;

	private Double substrateTemperature_3;

	private Double substrateTemperature_4;

	private Double substrateTemperature_5;

	private Double substrateTemperature_6;

	private Double substrateTemperature_7;

	private Double substrateTemperature_8;

	private Double substrateTemperature_9;

	private Double substrateTemperature_10;

	private Double substrateTemperature_11;

	private Double substrateTemperature_12;

	private Double substrateTemperature_13;

	private Double substrateTemperature_14;

	private Double temperatureCell_1;

	private Double temperatureCell_2;

	private Double temperatureCell_3;

	private Double temperatureCell_4;

	private Double temperatureCell_NaF;

	private Integer turboRpm;

	private Double substrateRotationSpeedRpm;

	private Double substrateLengthMM;

}
