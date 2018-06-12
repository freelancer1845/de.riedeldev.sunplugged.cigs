package de.riedeldev.sunplugged.cigs.logger.server.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;

public interface LogSessionRepository extends JpaRepository<LogSession, Long> {

    Stream<LogSession> findAllByOrderByIdDesc();

}
