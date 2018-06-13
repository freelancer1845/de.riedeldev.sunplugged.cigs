package de.riedeldev.sunplugged.cigs.logger.server.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;

public interface DataPointRepository extends JpaRepository<DataPoint, Long> {

    Stream<DataPoint> findAllBySession(LogSession session);

    Long countBySession(LogSession session);

    void deleteAllBySession(LogSession session);
    
}
