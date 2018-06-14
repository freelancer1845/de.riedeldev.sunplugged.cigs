package de.riedeldev.sunplugged.cigs.logger.server.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;

@RestController
public class TestService {

	private int i = 0;

	@GetMapping(path = "/data")
	public DataPoint getDataPoint() {

		DataPoint dataPoint = new DataPoint();
		dataPoint.setSubstrateTempOne(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setSubstrateTempTwo(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setSubstrateTempThree(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setSubstrateTempFour(Math.sin(i * Math.PI / 3600.0 * 2.0));

		i++;

		return dataPoint;
	}

}
