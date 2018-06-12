package de.riedeldev.sunplugged.cigs.logger.server.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class DataPoint {
	
	@Id
	@GeneratedValue
	private Long id;
	
	private Double substrateOneTemp;
	
}
