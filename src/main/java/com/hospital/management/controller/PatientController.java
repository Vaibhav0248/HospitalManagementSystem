package com.hospital.management.controller;

import com.hospital.management.entity.Patient;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private com.hospital.management.service.DoctorService doctorService;

    @Autowired
    private com.hospital.management.service.AppointmentService appointmentService;

    @GetMapping("/list")
    public String listPatients(Model model, org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
            com.hospital.management.entity.Doctor doctor = doctorService.getDoctorByUsername(authentication.getName());
            if (doctor != null) {
                java.util.List<com.hospital.management.entity.Appointment> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());
                java.util.List<com.hospital.management.entity.Patient> doctorPatients = appointments.stream()
                        .map(com.hospital.management.entity.Appointment::getPatient)
                        .distinct()
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("patients", doctorPatients);
            } else {
                model.addAttribute("patients", java.util.Collections.emptyList());
            }
        } else {
            model.addAttribute("patients", patientService.getAllPatients());
        }
        return "patients";
    }

    @GetMapping("/add")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "patient_form";
    }

    @PostMapping("/save")
    public String savePatient(@ModelAttribute("patient") Patient patient) {
        patientService.savePatient(patient);
        return "redirect:/patient/list";
    }

    @GetMapping("/edit/{id}")
    public String editPatientForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        return "patient_form";
    }

    @GetMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return "redirect:/patient/list";
    }

    @GetMapping("/dashboard")
    public String patientDashboard(Model model, org.springframework.security.core.Authentication authentication) {
        if (authentication != null) {
            // Find the patient matching this user details
            String username = authentication.getName();
            // We need to fetch patient but currently patientService doesn't have a findByUsername.
            // For the sake of simplicity without modifying User schema heavily:
            // Let's filter in memory since this is a small system.
            Patient currentPatient = patientService.getAllPatients().stream()
                    .filter(p -> p.getUser() != null && username.equals(p.getUser().getUsername()))
                    .findFirst()
                    .orElse(null);
            
            if (currentPatient != null) {
                model.addAttribute("myAppointments", com.hospital.management.config.StaticContextAccessor.getBean(com.hospital.management.service.AppointmentService.class).getAppointmentsByPatientId(currentPatient.getId()));
            } else {
                 model.addAttribute("myAppointments", java.util.Collections.emptyList());
            }
            
            model.addAttribute("doctors", com.hospital.management.config.StaticContextAccessor.getBean(com.hospital.management.service.DoctorService.class).getAllDoctors());
        }
        return "patient_dashboard";
    }
}
