package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSettings;

@RestController
public class TestService {

	private int i = 0;

	@GetMapping(path = "/data")
	public ResponseEntity<DataPoint> getDataPoint() {

		DataPoint dataPoint = new DataPoint();
		// dataPoint.setPressureBakingPa(Math.sin(i * Math.PI / 3600.0 * 2.0));
		// dataPoint.setPressureFullRangeuPa_1(
		// Math.sin(i * Math.PI / 3600.0 * 2.0));
		// dataPoint.setPressureFullRangeuPa_2(
		// Math.sin(i * Math.PI / 3600.0 * 2.0));
		// dataPoint.setMagnetron_1_I((int) Math.sin(i * Math.PI / 3600.0 *
		// 2.0));

		Arrays.stream(DataPoint.class.getDeclaredFields()).filter(
				field -> field.getDeclaredAnnotation(LogSettings.class) != null)
				.filter(field -> field.getType() != LocalDateTime.class)
				.forEach(field -> {

					try {
						PropertyDescriptor desc = PropertyUtils
								.getPropertyDescriptor(dataPoint,
										field.getName());
						if (field.getType() == Integer.class) {
							desc.getWriteMethod().invoke(dataPoint,
									(int) randomData());
						} else if (field.getType() == Double.class) {
							desc.getWriteMethod().invoke(dataPoint,
									randomData());
						}

					} catch (IllegalAccessException | InvocationTargetException
							| NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});

		i++;
		// if (i > 20) {
		// i = 1;
		// }

		return ResponseEntity.ok(dataPoint);
	}

	private double randomData() {
		// if (i < 10) {
		// return 0.;
		// } else if (i < 20) {
		// return 1.;
		// } else {
		// return 0.;
		// }
		// return Math.sin(i * Math.PI / 1800.0 * 2.0); // sinus data
		return new Random().nextDouble() * 5;
	}

}
