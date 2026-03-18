package com.hospital.management.service;

import com.hospital.management.entity.Billing;
import java.util.List;

public interface BillingService {
    List<Billing> getAllBillings();
    Billing getBillingById(Long id);
    List<Billing> getBillingsByPatientId(Long patientId);
    Billing saveBilling(Billing billing);
    void deleteBilling(Long id);
}
