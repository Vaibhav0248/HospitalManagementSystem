package com.hospital.management.repository;

import com.hospital.management.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    List<Billing> findByPatientId(Long patientId);
    java.util.Optional<Billing> findByAppointmentId(Long appointmentId);
}
