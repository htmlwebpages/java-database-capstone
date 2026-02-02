package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService, Service service, AppointmentService appointmentService) {
            this.prescriptionService = prescriptionService;
            this.service = service;
            this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        appointmentService.updateAppointmentStatus(prescription.getAppointmentId());
        prescriptionService.savePrescription(prescription);
        return ResponseEntity.status(201)
                .body(Map.of("message", "Prescription saved successfully"));
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(validation.getStatusCode())
                .body(Map.of("message", validation.getBody().get("message")));
        }
        Prescription prescription = prescriptionService.getPrescription(appointmentId);
        if (prescription == null) {
            return ResponseEntity.status(404)
                .body(Map.of("message", "No prescription found for this appointment"));
        }
        return ResponseEntity.ok(prescription);
    }
}