package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.repository.LogSessionRepository;

@Component
@Service
public class DataLoggingService {

	public interface LiveListener {
		void newDataPoint(DataPoint point);
	}

	private LogFileService logFileService;

	private LogSessionRepository sessionRepo;

	private List<LiveListener> liveListeners = new LinkedList<>();

	@Autowired
	public DataLoggingService(LogFileService logFileService,
			LogSessionRepository sessionRepo) {
		this.logFileService = logFileService;
		this.sessionRepo = sessionRepo;
	}

	public List<LogSession> getSessions() {
		return sessionRepo.findAll();
	}

	public void registerAsListener(LiveListener listener) {
		liveListeners.add(listener);
	}

	public LogSession addDataPoint(DataPoint point, LogSession logSession) {
		if (point.getDateTime() == null) {
			point.setDateTime(LocalDateTime.now());
		}
		LogSession session = sessionRepo.findById(logSession.getId())
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"No Session with id %d exists.", logSession.getId())));
		if (session.getStartDate() == null) {
			session.setStartDate(point.getDateTime());
		}

		session.setEndDate(point.getDateTime());

		logFileService.saveDataPoint(point, logSession);

		liveListeners.forEach(listener -> listener.newDataPoint(point));
		return sessionRepo.save(session);
	}

	public List<DataPoint> getDatapointsOfSession(LogSession session)
			throws IOException {
		return logFileService.getDataPoints(session);
	}

	// TODO : This currently uses a hack to avoid transactional error...
	@Transactional
	public Stream<LogSession> streamSessionsFromLast() {
		Stream<LogSession> stream = sessionRepo.findAllByOrderByIdDesc();
		List<LogSession> sessions = stream.collect(Collectors.toList());

		return sessions.stream().filter(session -> {
			File file = new File(session.getLogFilePath());
			return file.exists();

		});
	}

	public LogSession createNewSession() throws IOException {
		LogSession session = new LogSession();
		session.setStartDate(LocalDateTime.now());
		logFileService.createLogFileForLogSession(session);
		session.setEndDate(LocalDateTime.now().plusSeconds(1));
		return sessionRepo.save(session);
	}

	public Long sessionsCount() {
		return sessionRepo.count();
	}

	public void deleteLogSession(LogSession session) {
		sessionRepo.delete(session);
		logFileService.deleteDataFileForLogSession(session);
	}

	@Transactional(readOnly = true)
	public LogSession getLiveLogSession(LogSession session) {
		return sessionRepo.findById(session.getId()).orElse(null);
	}

}
