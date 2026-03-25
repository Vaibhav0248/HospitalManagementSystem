package com.hospital.management.service.impl;

import com.hospital.management.entity.Billing;
import com.hospital.management.repository.BillingRepository;
import com.hospital.management.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Override
    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    @Override
    public Billing getBillingById(Long id) {
        return billingRepository.findById(id).orElse(null);
    }

    @Override
    public List<Billing> getBillingsByPatientId(Long patientId) {
        return billingRepository.findByPatientId(patientId);
    }

    @Override
    public Billing getBillingByAppointmentId(Long appointmentId) {
        return billingRepository.findByAppointmentId(appointmentId).orElse(null);
    }

    @Override
    public Billing saveBilling(Billing billing) {
        return billingRepository.save(billing);
    }

    @Override
    public void deleteBilling(Long id) {
        billingRepository.deleteById(id);
    }
}
