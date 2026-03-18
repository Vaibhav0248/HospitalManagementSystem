package com.hospital.management.controller;

import com.hospital.management.entity.Appointment;
import com.hospital.management.entity.Billing;
import com.hospital.management.service.AppointmentService;
import com.hospital.management.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private BillingService billingService;

    // --- Appointment REST APIs ---

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.saveAppointment(appointment));
    }

    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    // --- Real-Time Patient & Doctor Endpoints ---

    @GetMapping("/doctors/{doctorId}/appointments")
    public ResponseEntity<List<Appointment>> getDoctorAppointmentsForDate(
            @PathVariable Long doctorId,
            @RequestParam String date) {
        java.time.LocalDate requestedDate = java.time.LocalDate.parse(date);
        return ResponseEntity.ok(appointmentService.getAppointmentsForDoctorOnDate(doctorId, requestedDate));
    }

    @GetMapping("/appointments/{id}/wait-time")
    public ResponseEntity<Integer> getEstimatedWaitTime(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.calculateWaitTime(id));
    }

    // --- Billing REST APIs ---

    @GetMapping("/billings")
    public ResponseEntity<List<Billing>> getAllBillings() {
        return ResponseEntity.ok(billingService.getAllBillings());
    }

    @GetMapping("/billings/{id}")
    public ResponseEntity<Billing> getBillingById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillingById(id));
    }

    @PostMapping("/billings")
    public ResponseEntity<Billing> createBilling(@RequestBody Billing billing) {
        return ResponseEntity.ok(billingService.saveBilling(billing));
    }

    @DeleteMapping("/billings/{id}")
    public ResponseEntity<Void> deleteBilling(@PathVariable Long id) {
        billingService.deleteBilling(id);
        return ResponseEntity.noContent().build();
    }
}
