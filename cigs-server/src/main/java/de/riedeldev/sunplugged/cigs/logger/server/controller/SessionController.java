package de.riedeldev.sunplugged.cigs.logger.server.controller;

import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.cigs.logger.server.model.DataPoint;
import de.riedeldev.sunplugged.cigs.logger.server.model.LogSession;
import de.riedeldev.sunplugged.cigs.logger.server.service.DataLoggingService;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * SessionController
 */
@RestController
@Slf4j
@CrossOrigin
public class SessionController {

    private static final String API = "api/sessions";

    @Autowired
    private DataLoggingService service;

    @GetMapping(value = API + "")
    public List<LogSession> getMethodName() {
        return service.getSessions();
    }

    @GetMapping(value = API + "/loadcsv")
    public ResponseEntity<Resource> loadSessionAsCSV(@RequestParam long id) {
        try {
            Resource resource = new InputStreamResource(service.getDownloadStreamForLogId(id));
            return ResponseEntity.ok().body(resource);
        } catch (FileNotFoundException e) {
            log.error("Resource requested that does not exist. Session ID: " + id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = API + "/loadraw")
    public List<DataPoint> loadDataPoints(@RequestParam long id) throws IOException {
        return service.getDatapointsOfSessionById(id);
    }

    @GetMapping(value = API + "/delete")
    public ResponseEntity<?> deleteSession(@RequestParam long id) {
        service.deleteLogSessionById(id);
        return ResponseEntity.ok().build();
    }

}