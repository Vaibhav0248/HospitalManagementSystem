package com.hospital.management.controller;

import com.hospital.management.entity.Appointment;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.DoctorService;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @GetMapping("/list")
    public String listAppointments(Model model, org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            com.hospital.management.entity.Doctor doctor = doctorService.getDoctorByUsername(authentication.getName());
            if (doctor != null) {
                model.addAttribute("appointments", appointmentService.getAppointmentsByDoctorId(doctor.getId()));
            } else {
                model.addAttribute("appointments", java.util.Collections.emptyList());
            }
        } else {
            model.addAttribute("appointments", appointmentService.getAllAppointments());
        }
        return "appointments";
    }

    @GetMapping("/add")
    public String addAppointmentForm(Model model, org.springframework.security.core.Authentication authentication) {
        if (!model.containsAttribute("appointment")) {
            model.addAttribute("appointment", new Appointment());
        }
        model.addAttribute("doctors", doctorService.getAllDoctors());
        
        if (authentication != null && authentication.getAuthorities().contains(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_PATIENT"))) {
            String username = authentication.getName();
            com.hospital.management.entity.Patient currentPatient = patientService.getAllPatients().stream()
                    .filter(p -> p.getUser() != null && username.equals(p.getUser().getUsername()))
                    .findFirst()
                    .orElse(null);
            
            if (currentPatient != null) {
                model.addAttribute("patients", java.util.Collections.singletonList(currentPatient));
                model.addAttribute("isPatientRole", true);
            } else {
                model.addAttribute("patients", java.util.Collections.emptyList());
                model.addAttribute("isPatientRole", true);
            }
        } else {
            model.addAttribute("patients", patientService.getAllPatients());
            model.addAttribute("isPatientRole", false);
        }
        
        return "appointment_form";
    }

    @PostMapping("/save")
    public String saveAppointment(@ModelAttribute("appointment") Appointment appointment, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            if (appointment.getId() == null || appointment.getStatus() == null) {
                appointment.setStatus("SCHEDULED");
            }
            appointmentService.saveAppointment(appointment);
            return "redirect:/appointment/list";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("appointment", appointment);
            return "redirect:/appointment/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        model.addAttribute("doctors", doctorService.getAllDoctors());
        model.addAttribute("patients", patientService.getAllPatients());
        return "appointment_form";
    }

    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, @RequestHeader(value = "referer", required = false) String referer) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            appointment.setStatus("CANCELLED");
            appointmentService.saveAppointment(appointment);
        }
        return "redirect:" + (referer != null ? referer : "/appointment/list");
    }
}
