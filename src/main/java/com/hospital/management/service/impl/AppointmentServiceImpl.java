package com.hospital.management.service.impl;

import com.hospital.management.entity.Appointment;
import com.hospital.management.repository.AppointmentRepository;
import com.hospital.management.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public List<Appointment> getAllAppointments() {
        return updatePastAppointments(appointmentRepository.findAll());
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return updatePastAppointments(appointmentRepository.findByDoctorId(doctorId));
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return updatePastAppointments(appointmentRepository.findByPatientId(patientId));
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        // Double Booking Prevention & Emergency Override
        if (appointment.getId() == null) {
            LocalDateTime startTime = appointment.getAppointmentDateTime();
            LocalDateTime endTime = startTime.plusMinutes(60);

            // Fetch all overlapping appointments
            List<Appointment> overlaps = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(
                    appointment.getDoctor().getId(),
                    startTime.minusMinutes(59), // Prevents overlapping within 1 hour
                    endTime);


            if (!overlaps.isEmpty()) {
                if ("EMERGENCY".equalsIgnoreCase(appointment.getPriority())) {
                    // Check if there are any existing EMERGENCY appointments in this slot
                    boolean hasEmergencyOverlap = overlaps.stream()
                            .anyMatch(a -> "EMERGENCY".equalsIgnoreCase(a.getPriority()));

                    if (hasEmergencyOverlap) {
                        throw new RuntimeException("This time slot is already booked for an EMERGENCY.");
                    }

                    // Move existing NORMAL appointments by 1 hour
                    for (Appointment overlap : overlaps) {
                        overlap.setAppointmentDateTime(overlap.getAppointmentDateTime().plusHours(1));
                        overlap.setNotes((overlap.getNotes() == null ? "" : overlap.getNotes() + "\n")
                                + "(Rescheduled +1 hr due to Emergency)");
                        appointmentRepository.save(overlap);
                    }
                } else {
                    // Normal appointment cannot override anything
                    throw new RuntimeException("This time slot is already booked for the selected doctor.");
                }
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
        return updatePastAppointments(
                appointmentRepository.findAppointmentsForDoctorOnDate(doctorId, startOfDay, endOfDay));
    }

    private List<Appointment> updatePastAppointments(List<Appointment> appointments) {
        boolean updated = false;
        LocalDateTime now = LocalDateTime.now();
        java.util.List<Appointment> toUpdate = new java.util.ArrayList<>();

        for (Appointment a : appointments) {
            if ("SCHEDULED".equals(a.getStatus()) && a.getAppointmentDateTime().isBefore(now)) {
                a.setStatus("COMPLETED");
                toUpdate.add(a);
                updated = true;
            }
        }

        if (updated) {
            appointmentRepository.saveAll(toUpdate);
        }

        return appointments;
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
            if ("SCHEDULED".equals(earlierAppt.getStatus())
                    && earlierAppt.getAppointmentDateTime().isAfter(LocalDateTime.now())) {
                waitTimeMinutes += 60; // Assume 60 minutes per patient
            }
        }

        return waitTimeMinutes;
    }
}
