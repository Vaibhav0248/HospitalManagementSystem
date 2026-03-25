package com.hospital.management.controller;

import com.hospital.management.entity.Billing;
import com.hospital.management.service.BillingService;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private com.hospital.management.service.AppointmentService appointmentService;

    @GetMapping("/list")
    public String listBillings(Model model) {
        model.addAttribute("billings", billingService.getAllBillings());
        return "billings";
    }

    @GetMapping("/generate/{appointmentId}")
    public String generateBillForm(@PathVariable Long appointmentId, Model model) {
        com.hospital.management.entity.Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            return "redirect:/appointment/list";
        }

        Billing billing = billingService.getBillingByAppointmentId(appointmentId);
        if (billing == null) {
            billing = new Billing();
            billing.setAppointment(appointment);
            billing.setPatient(appointment.getPatient());
            billing.setBillingDate(java.time.LocalDate.now());
            billing.setStatus("PENDING");
            billing.setConsultationFee(500.0);
            billing.setTestType("None");
            billing.setTestFee(0.0);
            billing.setMedicinesFee(0.0);
            billing.setDiscount(0.0);
            billing.setAmount(500.0); // will be re-calculated
        }

        model.addAttribute("billing", billing);
        model.addAttribute("appointment", appointment);
        return "billing_generate";
    }

    @PostMapping("/generate/{appointmentId}")
    public String saveGeneratedBill(@PathVariable Long appointmentId, @ModelAttribute("billing") Billing billing) {
        com.hospital.management.entity.Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment != null) {
            Billing existingBilling = billingService.getBillingByAppointmentId(appointmentId);
            if (existingBilling == null) {
                existingBilling = new Billing();
                existingBilling.setBillingDate(java.time.LocalDate.now());
                existingBilling.setStatus("PAID");
                existingBilling.setDescription("Generated Bill");
            }

            existingBilling.setAppointment(appointment);
            existingBilling.setPatient(appointment.getPatient());

            existingBilling.setConsultationFee(billing.getConsultationFee());
            existingBilling.setTestType(billing.getTestType());
            existingBilling.setTestFee(billing.getTestFee());
            existingBilling.setMedicinesFee(billing.getMedicinesFee());
            existingBilling.setDiscount(billing.getDiscount());
            existingBilling.setPaymentMode(billing.getPaymentMode());

            Double consultation = existingBilling.getConsultationFee() != null ? existingBilling.getConsultationFee()
                    : 0.0;
            Double test = existingBilling.getTestFee() != null ? existingBilling.getTestFee() : 0.0;
            Double medicines = existingBilling.getMedicinesFee() != null ? existingBilling.getMedicinesFee() : 0.0;
            Double discount = existingBilling.getDiscount() != null ? existingBilling.getDiscount() : 0.0;

            Double subtotal = consultation + test + medicines;
            Double subtotalAfterDiscount = subtotal - discount;
            Double gst = subtotalAfterDiscount * 0.18; // 18% GST
            Double total = subtotalAfterDiscount + gst;

            existingBilling.setGst(gst);
            existingBilling.setTotalAmount(total);
            existingBilling.setAmount(total);

            Billing savedBilling = billingService.saveBilling(existingBilling);
            return "redirect:/billing/print/" + savedBilling.getId();
        }
        return "redirect:/appointment/list";
    }

    @GetMapping("/print/{id}")
    public String printBill(@PathVariable Long id, Model model) {
        Billing billing = billingService.getBillingById(id);
        if (billing == null) {
            return "redirect:/billing/list";
        }
        model.addAttribute("billing", billing);
        return "bill_print";
    }

    @GetMapping("/add")
    public String addBillingForm(Model model) {
        mmodel.ddAttribute("patients", patie

        return "billing_form";
    }

    @PostMapping("/save")
    public String saveBilling(@ModelAttribute("billing") Billing billing) {
        billingService.saveBilling(billing);
        return "redirect:/billing/list";
    }

    @GetMapping("/edit/{id}")
    public String editBillingForm(@PathVariable Long id, Model model) {
        model.addAttribute("billing", billingService.getBillingById(id));
        model.addAttribute("patients", patientService.getAllPatients());
        return "billing_form";
    }

    @GetMapping("/delete/{id}")
    public String deleteBilling(@PathVariable Long id) {
        billingService.deleteBilling(id);
        return "redirect:/billing/list";
    }
}
