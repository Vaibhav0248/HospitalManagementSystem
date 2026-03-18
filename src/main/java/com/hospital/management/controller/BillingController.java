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

    @GetMapping("/list")
    public String listBillings(Model model) {
        model.addAttribute("billings", billingService.getAllBillings());
        return "billings";
    }

    @GetMapping("/add")
    public String addBillingForm(Model model) {
        model.addAttribute("billing", new Billing());
        model.addAttribute("patients", patientService.getAllPatients());
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
