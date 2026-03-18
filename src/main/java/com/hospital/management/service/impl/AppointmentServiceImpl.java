package com.hospital.management.service.impl;

import com.hospital.management.entity.Appointment;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        // Double Booking Prevention
        if (appointment.getId() == null) {
            // Check if slot is taken (assume 15 minute slots)
            LocalDateTime startTime = appointment.getAppointmentDateTime();
            LocalDateTime endTime = startTime.plusMinutes(15);
            
            boolean isBooked = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeBetween(
                    appointment.getDoctor().getId(), 
                    startTime.minusMinutes(14), // Prevents overlapping
                    endTime
            );
            
            if (isBooked) {
                throw new RuntimeException("This time slot is already booked for the selected doctor.");
            }
        }
        
        return appointmentRepository.save(appointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointment> getAppointmentsForDoctorOnDate(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return appointmentRepository.findAppointmentsForDoctorOnDate(doctorId, startOfDay, endOfDay);
    }

    @Override
    public int calculateWaitTime(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (appointment == null || !"SCHEDULED".equals(appointment.getStatus())) {
            return 0; // No wait time if not scheduled or invalid
        }

        LocalDate date = appointment.getAppointmentDateTime().toLocalDate();
        List<Appointment> dailyAppointments = getAppointmentsForDoctorOnDate(appointment.getDoctor().getId(), date);

        int waitTimeMinutes = 0;
        for (Appointment earlierAppt : dailyAppointments) {
            if (earlierAppt.getId().equals(appointmentId)) {
                break; // Reached current appointment
            }
            // Only count scheduled appointments that haven't happened yet
            if ("SCHEDULED".equals(earlierAppt.getStatus()) && earlierAppt.getAppointmentDateTime().isAfter(LocalDateTime.now())) {
                waitTimeMinutes += 15; // Assume 15 minutes per patient
            }
        }
        
        return waitTimeMinutes;
    }
}
