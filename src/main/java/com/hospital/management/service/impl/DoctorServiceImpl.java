package com.hospital.management.service.impl;

import com.hospital.management.entity.Doctor;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.RoleRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    @Override
    public Doctor getDoctorByUsername(String username) {
        return doctorRepository.findByUserUsername(username).orElse(null);
    }

    @Override
    public Doctor saveDoctor(Doctor doctor) {
        if (doctor.getId() == null && doctor.getUser() == null) {
            com.hospital.management.entity.User user = new com.hospital.management.entity.User();
            String generatedUsername = (doctor.getFirstName() + "." + doctor.getLastName()).toLowerCase().replaceAll("\\s+", "");
            user.setUsername(generatedUsername);
            String rawPassword = doctor.getLastName().toLowerCase().replaceAll("\\s+", "") + "123";
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEnabled(true);

            com.hospital.management.entity.Role doctorRole = roleRepository.findByName("ROLE_DOCTOR").orElse(null);
            if (doctorRole != null) {
                user.getRoles().add(doctorRole);
            }
            user = userRepository.save(user);
            doctor.setUser(user);
        }
        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}
