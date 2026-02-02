package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.dto.Login;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public int saveDoctor(Doctor doctor) {
        try {
            Doctor existing = doctorRepository.findByEmail(doctor.getEmail());
            if (existing != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existing = doctorRepository.findById(doctor.getId());
            if (existing.isEmpty()) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();

        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(doctor.getId(), "DOCTOR");
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors = filterDoctorByTime(doctors, amOrPm);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
                .filter(d -> d.getAvailableTimes().stream()
                .anyMatch(t -> t.toUpperCase().contains(amOrPm.toUpperCase()))
                ).collect(Collectors.toList());
    }
}