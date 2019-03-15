package de.riedeldev.sunplugged.cigs.logger.server.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.riedeldev.sunplugged.cigs.logger.server.service.DataAquisitionService;

/**
 * LogController
 */
@RestController
@CrossOrigin
public class LogController {

    @Autowired
    private DataAquisitionService service;

    private static final String API = "api/log/";

    @GetMapping(value = API + "state")
    public Map<String, Object> getState() {
        Map<String, Object> data = new HashMap<>();
        data.put("state", service.isLogging());
        if (service.isLogging()) {
            data.put("session", service.getActiveSession());
        }
        return data;
    }

    @GetMapping(value = API + "start")
    public ResponseEntity<?> start(@RequestParam Long speed) {
        try {
            service.setTimeStepSize(speed);
            service.startLogging();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("IOException on start. " + e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = API + "stop")
    public ResponseEntity<?> stop() {
        service.stopLogging();
        return ResponseEntity.ok().build();
    }

}