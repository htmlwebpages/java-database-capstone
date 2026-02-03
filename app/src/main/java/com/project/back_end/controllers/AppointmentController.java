package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.appService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {


    private final AppointmentService appointmentService;
    private final appService service;

    public AppointmentController(AppointmentService appointmentService, appService service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return ResponseEntity.ok(appointmentService.getAppointment(patientName, LocalDate.parse(date), token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        int result = service.validateAppointment(appointment);
        if (result != 1) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Appointment validation failed"));
        }
        int res = appointmentService.bookAppointment(appointment);
        if (res == 1) {
            return ResponseEntity.ok(Map.of("message", "Appointment booked successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Failed to book appointment"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        return appointmentService.cancelAppointment(id, token);
    }
}