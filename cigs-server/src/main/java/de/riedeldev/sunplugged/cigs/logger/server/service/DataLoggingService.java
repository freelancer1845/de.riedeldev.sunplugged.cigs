package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.repository.DataPointRepository;
import de.riedeldev.sunplugged.cigs.logger.server.repository.LogSessionRepository;

@Component
@Service
public class DataLoggingService {

	public interface LiveListener {
		void newDataPoint(DataPoint point);
	}

	private LogSessionRepository sessionRepo;

	private DataPointRepository dataRepo;

	private List<LiveListener> liveListeners = new LinkedList<>();

	@Autowired
	public DataLoggingService(LogSessionRepository sessionRepo, DataPointRepository dataRepo) {
		this.sessionRepo = sessionRepo;
		this.dataRepo = dataRepo;
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
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("No Session with id %d exists.", logSession.getId())));
		if (session.getStartDate() == null) {
			session.setStartDate(point.getDateTime());
		}

		session.setEndDate(point.getDateTime());
		point.setSession(session);
		DataPoint savedPoint = dataRepo.save(point);

		liveListeners.forEach(listener -> listener.newDataPoint(savedPoint));
		return sessionRepo.save(session);
	}

	@Transactional
	public List<DataPoint> getDatapointsOfSession(LogSession session) {
		return dataRepo.findAllBySession(session);
	}

	// TODO : This currently uses a hack to avoid transactional error...
	@Transactional
	public Stream<LogSession> streamSessionsFromLast() {
		Stream<LogSession> stream = sessionRepo.findAllByOrderByIdDesc();
		List<LogSession> sessions = stream.collect(Collectors.toList());

		return sessions.stream();
	}

	public LogSession createNewSession() {
		LogSession session = new LogSession();
		session.setStartDate(LocalDateTime.now());
		return sessionRepo.save(session);
	}

	public Long sessionsCount() {
		return sessionRepo.count();
	}

	public Long getCountOfDataPointsBySession(LogSession session) {
		return dataRepo.countBySession(session);
	}

	public void deleteLogSession(LogSession session) {
		dataRepo.deleteAllBySession(session);
		sessionRepo.delete(session);
	}

}
