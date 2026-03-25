package com.hospital.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "billing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private Double amount; // Kept for backwards compatibility but not really needed if we use totalAmount

    @Column
    private Double consultationFee;

    @Column
    private String testType;

    @Column
    private Double testFee;

    @Column
    private Double medicinesFee;

    @Column
    private Double discount;

    @Column
    private Double gst;

    @Column
    private Double totalAmount;

    @Column
    private String paymentMode;

    @Column(nullable = false)
    private String status; // PENDING, PAID, CANCELLED

    @Column(nullable = false)
    private LocalDate billingDate;

    @Column(columnDefinition = "TEXT")
    private String description;
}
