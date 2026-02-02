package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.appService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final appService service;

    public PatientController(PatientService patientService, appService service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        boolean isValid = service.validatePatient(patient);
        if (!isValid) {
            return ResponseEntity.status(409)
                .body(Map.of("message","Patient with email id or phone no already exist"));
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Signup successful"));
        }
        return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return patientService.getPatientAppointment(id, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return service.filterPatient(condition, name, token);
    }
}