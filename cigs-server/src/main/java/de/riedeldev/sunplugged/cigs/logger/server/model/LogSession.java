package de.riedeldev.sunplugged.cigs.logger.server.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "LOG_SESSIONS")
@Data
public class LogSession {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}
