package com.hospital.management.service;

import com.hospital.management.entity.Appointment;
import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    List<Appointment> getAppointmentsByDoctorId(Long doctorId);
    List<Appointment> getAppointmentsByPatientId(Long patientId);
    Appointment saveAppointment(Appointment appointment);
    void deleteAppointment(Long id);
    
    List<Appointment> getAppointmentsForDoctorOnDate(Long doctorId, LocalDate date);
    int calculateWaitTime(Long appointmentId);
}
