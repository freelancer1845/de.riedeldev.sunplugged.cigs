package de.riedeldev.sunplugged.cigs.logger.server.service;

import org.springframework.stereotype.Service;

@Service
public class LogSettingsService {

	private static final String SETTINGS_FILE_NAME = "settings.txt";

	public static final String INTERVAL_SPEED = "intervalspeed";
	private static final Integer DEFAULT_INTERVAL_SPEED = 1000;

	public static final String AUTOMATIC_LOGGING = "automaticlogging";
	private static final Boolean DEFAULT_AUTOMATIC_LOGGING = true;

	public LogSettingsService() {
	}

}
