package de.riedeldev.sunplugged.cigs.logger.server.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;

@RestController
public class TestService {

	private int i = 0;

	@GetMapping(path = "/data")
	public ResponseEntity<DataPoint> getDataPoint() {

		DataPoint dataPoint = new DataPoint();
		dataPoint.setPressureBakingPa(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setPressureFullRangeuPa_1(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setPressureFullRangeuPa_2(Math.sin(i * Math.PI / 3600.0 * 2.0));
		dataPoint.setMagnetron_1_I((int) Math.sin(i * Math.PI / 3600.0 * 2.0));

		i++;
		
		if (i % 10 == 0) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(dataPoint);
	}

}
