package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;

@Service
public class LogFileService {

	@Value("${logfileservice.path}")
	private String path = "data";

	private static final String FILE_PREFIX = "cigs-";

	private static final String ENDING = ".csv";

	public boolean checkIfLogFileExists(LogSession logSession) {
		if (logSession.getLogFilePath() == null
				|| logSession.getLogFilePath().isEmpty()) {
			return false;
		}
		File logfile = new File(logSession.getLogFilePath());
		if (logfile.isDirectory()) {
			throw new IllegalStateException(
					"LogSession has a directory as logfile path attached!");
		}
		return logfile.exists();
	}

	public LogSession createLogFileForLogSession(LogSession logSession)
			throws IOException {
		logSession.setLogFilePath(generateUniquePath(logSession));

		File file = new File(logSession.getLogFilePath());
		file.createNewFile();
		return logSession;
	}

	private String generateUniquePath(LogSession logSession) {
		StringBuilder builder = new StringBuilder();

		builder.append(path);
		builder.append("/");
		builder.append(FILE_PREFIX);
		builder.append(logSession.getStartDate()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm")));
		String filePathWithoutEnding = builder.toString();

		File file;
		String filePath;
		int sameNameIndex = 0;
		do {
			if (sameNameIndex > 0) {
				filePath = filePathWithoutEnding + "-" + sameNameIndex + ENDING;
			} else {
				filePath = filePathWithoutEnding + ENDING;
			}
			file = new File(filePath);

		} while (file.exists());

		return file.getAbsolutePath();
	}

}
