package com.hospital.management.service;

import com.hospital.management.entity.Doctor;
import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(Long id);
    Doctor saveDoctor(Doctor doctor);
    void deleteDoctor(Long id);
}
