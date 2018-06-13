package de.riedeldev.sunplugged.cigs.logger.server.service;

import java.time.LocalDateTime;
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

    private LogSessionRepository sessionRepo;

    private DataPointRepository dataRepo;

    @Autowired
    public DataLoggingService(LogSessionRepository sessionRepo, DataPointRepository dataRepo) {
        this.sessionRepo = sessionRepo;
        this.dataRepo = dataRepo;
    }

    public List<LogSession> getSessions() {
        return sessionRepo.findAll();
    }

    public void addDataPoint(DataPoint point, Long sessionId) {
        LogSession session = sessionRepo.findById(sessionId)
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                String.format("No Session with id %d exists.", sessionId)));
        if (session.getStartDate() == null) {
            session.setStartDate(LocalDateTime.now());
        }

        session.setEndDate(point.getDateTime());
        point.setSession(session);
        dataRepo.save(point);
        sessionRepo.save(session);
    }

    public Stream<DataPoint> getDatapointsOfSessionAsStream(LogSession session) {
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
