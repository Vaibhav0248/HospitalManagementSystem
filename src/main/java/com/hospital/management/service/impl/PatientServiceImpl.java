package com.hospital.management.service.impl;

import com.hospital.management.entity.Patient;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.RoleRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Override
    public Patient savePatient(Patient patient) {
        if (patient.getId() == null && patient.getUser() == null) {
            com.hospital.management.entity.User user = new com.hospital.management.entity.User();
            String generatedUsername = (patient.getFirstName() + "." + patient.getLastName()).toLowerCase().replaceAll("\\s+", "");
            user.setUsername(generatedUsername);
            String rawPassword = patient.getLastName().toLowerCase().replaceAll("\\s+", "") + "123";
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEnabled(true);

            com.hospital.management.entity.Role patientRole = roleRepository.findByName("ROLE_PATIENT").orElse(null);
            if (patientRole != null) {
                user.getRoles().add(patientRole);

            }
            user = userRepository.save(user);
            patient.setUser(user);
        }
        return patientRepository.save(patient);
    }

    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
