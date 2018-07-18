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

	private Double pressureFullRangeuPa_1;

	@LogSettings(createChart = true, csvPosition = 4, nameToDisplay = "P Full Range 2 (Pa)")
	private Double pressureFullRangeuPa_2;

	@LogSettings(createChart = true, csvPosition = 5, nameToDisplay = "I Magnetron 1 (A)")
	private Integer magnetron_1_I;

	@LogSettings(createChart = true, csvPosition = 6, nameToDisplay = "I Magnetron 2 (A)")
	private Integer magnetron_2_I;

	@LogSettings(createChart = true, csvPosition = 7, nameToDisplay = "I Magnetron 3 (A)")
	private Integer magnetron_3_I;

	@LogSettings(createChart = true, csvPosition = 8, nameToDisplay = "I Magnetron 4 (A)")
	private Integer magnetron_4_I;

	@LogSettings(createChart = true, csvPosition = 9, nameToDisplay = "I Magnetron 5 (A)")
	private Integer magnetron_5_I;

	@LogSettings(createChart = true, csvPosition = 11, nameToDisplay = "I Magnetron 6 (A)")
	private Integer magnetron_6_I;

	@LogSettings(createChart = true, csvPosition = 12, nameToDisplay = "U Magnetron 1 (V)")
	private Integer magnetron_1_V;

	@LogSettings(createChart = true, csvPosition = 13, nameToDisplay = "U Magnetron 2 (V)")
	private Integer magnetron_2_V;

	@LogSettings(createChart = true, csvPosition = 14, nameToDisplay = "U Magnetron 3 (V)")
	private Integer magnetron_3_V;

	@LogSettings(createChart = true, csvPosition = 15, nameToDisplay = "U Magnetron 4 (V)")
	private Integer magnetron_4_V;

	@LogSettings(createChart = true, csvPosition = 16, nameToDisplay = "U Magnetron 5 (V)")
	private Integer magnetron_5_V;

	@LogSettings(createChart = true, csvPosition = 17, nameToDisplay = "U Magnetron 6 (V)")
	private Integer magnetron_6_V;

	@LogSettings(createChart = true, csvPosition = 18, nameToDisplay = "I Transfer Device 1 (A)")
	private Integer transferDevice_1_I;

	@LogSettings(createChart = true, csvPosition = 19, nameToDisplay = "U Transfer Device 1 (V)")
	private Integer transferDevice_1_V;

	@LogSettings(createChart = true, csvPosition = 20, nameToDisplay = "I Transfer Device 2 (A)")
	private Integer transferDevice_2_I;

	@LogSettings(createChart = true, csvPosition = 21, nameToDisplay = "U Transfer Device 2 (V)")
	private Integer transferDevice_2_V;

	@LogSettings(createChart = true, csvPosition = 22, nameToDisplay = "I Transfer Device 3 (A)")
	private Integer transferDevice_3_I;

	@LogSettings(createChart = true, csvPosition = 23, nameToDisplay = "U Transfer Device 3 (V)")
	private Integer transferDevice_3_V;

	@LogSettings(createChart = true, csvPosition = 24, nameToDisplay = "Substrate Temperature 1")
	private Double substrateTemperature_1;

	@LogSettings(createChart = true, csvPosition = 25, nameToDisplay = "Substrate Temperature 2")
	private Double substrateTemperature_2;

	@LogSettings(createChart = true, csvPosition = 26, nameToDisplay = "Substrate Temperature 3")
	private Double substrateTemperature_3;

	@LogSettings(createChart = true, csvPosition = 27, nameToDisplay = "Substrate Temperature 4")
	private Double substrateTemperature_4;

	@LogSettings(createChart = true, csvPosition = 28, nameToDisplay = "Substrate Temperature 5")
	private Double substrateTemperature_5;

	@LogSettings(createChart = true, csvPosition = 29, nameToDisplay = "Substrate Temperature 6")
	private Double substrateTemperature_6;

	@LogSettings(createChart = true, csvPosition = 30, nameToDisplay = "Substrate Temperature 7")
	private Double substrateTemperature_7;

	@LogSettings(createChart = true, csvPosition = 31, nameToDisplay = "Substrate Temperature 8")
	private Double substrateTemperature_8;

	@LogSettings(createChart = true, csvPosition = 32, nameToDisplay = "Substrate Temperature 9")
	private Double substrateTemperature_9;

	@LogSettings(createChart = true, csvPosition = 33, nameToDisplay = "Substrate Temperature 10")
	private Double substrateTemperature_10;

	@LogSettings(createChart = true, csvPosition = 34, nameToDisplay = "Substrate Temperature 11")
	private Double substrateTemperature_11;

	@LogSettings(createChart = true, csvPosition = 35, nameToDisplay = "Substrate Temperature 12")
	private Double substrateTemperature_12;

	@LogSettings(createChart = true, csvPosition = 36, nameToDisplay = "Substrate Temperature 13")
	private Double substrateTemperature_13;

	@LogSettings(createChart = true, csvPosition = 37, nameToDisplay = "Substrate Temperature 14")
	private Double substrateTemperature_14;

	@LogSettings(createChart = true, csvPosition = 38, nameToDisplay = "Cell Temperature 1")
	private Double temperatureCell_1;

	@LogSettings(createChart = true, csvPosition = 39, nameToDisplay = "Cell Temperature 2")
	private Double temperatureCell_2;

	@LogSettings(createChart = true, csvPosition = 40, nameToDisplay = "Cell Temperature 3")
	private Double temperatureCell_3;

	@LogSettings(createChart = true, csvPosition = 41, nameToDisplay = "Cell Temperature 4")
	private Double temperatureCell_4;
	
	@LogSettings(createChart = true, csvPosition = 42, nameToDisplay = "Manifold Temperature 1")
	private Double temperatureManifold_1;
	
	@LogSettings(createChart = true, csvPosition = 43, nameToDisplay = "Manifold Temperature 2")
	private Double temperatureManifold_2;
	
	@LogSettings(createChart = true, csvPosition = 44, nameToDisplay = "Manifold Temperature 3")
	private Double temperatureManifold_3;
	
	@LogSettings(createChart = true, csvPosition = 45, nameToDisplay = "Manifold Temperature 4")
	private Double temperatureManifold_4;

	@LogSettings(createChart = true, csvPosition = 46, nameToDisplay = "Cell Temperature NaF")
	private Double temperatureCell_NaF;

	@LogSettings(createChart = true, csvPosition = 47, nameToDisplay = "Turbo Pump Rpm")
	private Integer turboRpm;

	@LogSettings(createChart = true, csvPosition = 48, nameToDisplay = "Substrate Rotation Speed Rpm")
	private Double substrateRotationSpeedRpm;

	@LogSettings(createChart = true, csvPosition = 49, nameToDisplay = "Substrate Lenght (mm)")
	private Double substrateLengthMM;

}
