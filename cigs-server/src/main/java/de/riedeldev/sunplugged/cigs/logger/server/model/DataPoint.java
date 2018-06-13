package de.riedeldev.sunplugged.cigs.logger.server.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "DATA_POINTS")
@Data
public class DataPoint {

	@Id
	@GeneratedValue
	private Long id;

	private LocalDateTime dateTime;

	@ManyToOne(targetEntity = LogSession.class, optional = false)
	private LogSession session;

	private Double substrateTempOne;

	private Double substrateTempTwo;

	private Double substrateTempThree;

	private Double substrateTempFour;

}
