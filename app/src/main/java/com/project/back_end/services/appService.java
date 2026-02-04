package com.project.back_end.services;

import com.project.back_end.models.*;
import com.project.back_end.repo.*;
import com.project.back_end.dto.Login;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

@Service
public class appService {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public appService(TokenService tokenService, AdminRepository adminRepository,
                DoctorRepository doctorRepository, PatientRepository patientRepository,
                DoctorService doctorService,PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        return tokenService.validateToken(token, user);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Admin> adminOpt = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                if (admin.getPassword().equals(receivedAdmin.getPassword())) {
                    String token = tokenService.generateToken(admin.getUsername());
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                }
            }
            response.put("message", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterDoctor(String name, String speciality, String time) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.filterDoctorsByNameSpecialityandTime(name, speciality, time);
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    public int validateAppointment(Appointment appointment) {
        Doctor doctor = appointment.getDoctor();
        if (doctor == null || doctor.getId() == null) {
            return -1;
        }
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctor.getId());
        if (doctorOpt.isEmpty()) {
            return -1;
        }
        Long doctorId = doctorOpt.get().getId();
        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        List<String> availableSlots = doctorService.getDoctorAvailability(doctorId, date);
        String appointmentTime = appointment.getAppointmentTime().toLocalTime().toString();
        return availableSlots.contains(appointmentTime) ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        Optional<Patient> existingPatient = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existingPatient.isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Patient> patientOpt = patientRepository.findByEmail(login.getEmail());
            if (patientOpt.isPresent()) {
                Patient patient = patientOpt.get();
                if (patient.getPassword().equals(login.getPassword())) {
                    String token = tokenService.generateToken(patient.getEmail());
                    response.put("token", token);
                    return ResponseEntity.ok(response);
                }
            }
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Unauthorized"));
        }
        Patient patient = patientOpt.get();
        if (patient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Unauthorized"));
        }
        Long patientId = patient.getId();
        ResponseEntity<Map<String, Object>> response;
        if (condition != null && name != null) {
            response = patientService.filterByDoctorAndCondition(condition, name, patientId);
        }
        else if (condition != null) {
            response = patientService.filterByCondition(condition, patientId);
        }
        else if (name != null) {
            response = patientService.filterByDoctor(name, patientId);
        }
        else {
            response = patientService.getPatientAppointment(patientId, token);
        }
        return response;
    }
}