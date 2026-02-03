package com.project.back_end.mvc;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.back_end.services.appService;

@Controller
public class DashboardController {

    @Autowired
    private appService service;

    @GetMapping("/adminDashboard/{token}") 
    public String adminDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "admin");
        if (validationResult.getStatusCode().is2xxSuccessful()) {
            return "admin/adminDashboard";
        }
        return "redirect:http://localhost:8080";
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResult = service.validateToken(token, "doctor");
        if (validationResult.getStatusCode().is2xxSuccessful()) {
            return "doctor/doctorDashboard";
        }

        return "redirect:http://localhost:8080";
    }
}