package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.dto.Login;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository, TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Doctor doctor = doctorOpt.get();
        List<String> availableSlots = new ArrayList<>(doctor.getAvailableTimes());

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59);
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        List<String> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());

        availableSlots.removeAll(bookedSlots);
        return availableSlots;
    }

    public ResponseEntity<Map<String, String>> saveDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
        return ResponseEntity.ok(Map.of("message", "Doctor saved successfully"));
    }

    public ResponseEntity<Map<String, String>> updateDoctor(Doctor doctor) {
        doctorRepository.save(doctor);
        return ResponseEntity.ok(Map.of("message", "Doctor updated successfully"));
    }

    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(d -> {
            if (d.getAvailableTimes() == null) {
                d.setAvailableTimes(new ArrayList<>());
            }
        });
        return doctors;
    }

    public ResponseEntity<Map<String, String>> deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(login.getEmail());
        if (doctorOpt.isEmpty()) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }
    
        Doctor doctor = doctorOpt.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(login.getPassword(), doctor.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(doctor.getEmail());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
    

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike("%" + name + "%");
    }


    @Transactional
    public List<Doctor> filterDoctorsByNameSpecialityandTime(String name, String speciality, String amOrPm) {
        List<Doctor> doctors;
        if (name == null || name.equalsIgnoreCase("null") || name.isBlank()) {
            doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        }
        else {
            doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
    }
    return filterDoctorByTime(doctors, amOrPm);
}

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        return filterDoctorByTime(doctors, amOrPm);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpeciality(String name, String speciality) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialityIgnoreCase(name, speciality);
    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpeciality(String speciality, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialityIgnoreCase(speciality);
        return filterDoctorByTime(doctors, amOrPm);
    }

    @Transactional
    public List<Doctor> filterDoctorBySpeciality(String speciality) {
        return doctorRepository.findBySpecialityIgnoreCase(speciality);
    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        return filterDoctorByTime(doctors, amOrPm);
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
    if (amOrPm == null || amOrPm.equalsIgnoreCase("all")) {
        return doctors;
    }

    return doctors.stream()
        .filter(d -> {
            List<String> times = d.getAvailableTimes();
            if (times == null) return false;

            return times.stream()
                .anyMatch(t -> t.toUpperCase().contains(amOrPm.toUpperCase()));
        })
        .collect(Collectors.toList());
}
}