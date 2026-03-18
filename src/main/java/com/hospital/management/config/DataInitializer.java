package com.hospital.management.config;

import com.hospital.management.entity.Doctor;
import com.hospital.management.entity.Patient;
import com.hospital.management.entity.Role;
import com.hospital.management.entity.User;
import com.hospital.management.repository.DoctorRepository;
import com.hospital.management.repository.PatientRepository;
import com.hospital.management.repository.RoleRepository;
import com.hospital.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, UserRepository userRepository, DoctorRepository doctorRepository, PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_ADMIN");
                return roleRepository.save(role);
            });

            Role doctorRole = roleRepository.findByName("ROLE_DOCTOR").orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_DOCTOR");
                return roleRepository.save(role);
            });
            
            Role patientRole = roleRepository.findByName("ROLE_PATIENT").orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_PATIENT");
                return roleRepository.save(role);
            });
            
            Role staffRole = roleRepository.findByName("ROLE_STAFF").orElseGet(() -> {
                Role role = new Role();
                role.setName("ROLE_STAFF");
                return roleRepository.save(role);
            });

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                HashSet<Role> roles = new HashSet<>();
                roles.add(adminRole);
                admin.setRoles(roles);
                admin.setEnabled(true);
                userRepository.save(admin);
            }
            
            User finalDoctor = userRepository.findByUsername("doctor").orElseGet(() -> {
                User doctor = new User();
                doctor.setUsername("doctor");
                doctor.setPassword(passwordEncoder.encode("doctor123"));
                HashSet<Role> roles = new HashSet<>();
                roles.add(doctorRole);
                doctor.setRoles(roles);
                doctor.setEnabled(true);
                return userRepository.save(doctor);
            });
            
            if (doctorRepository.findAll().stream().noneMatch(d -> d.getUser() != null && "doctor".equals(d.getUser().getUsername()))) {
                Doctor doctorProfile = new Doctor();
                doctorProfile.setUser(finalDoctor);
                doctorProfile.setFirstName("Jane");
                doctorProfile.setLastName("Doe");
                doctorProfile.setEmail("doctor@hospital.com");
                doctorProfile.setPhone("1234567890");
                doctorProfile.setSpecialization("Cardiology");
                doctorRepository.save(doctorProfile);
            }

            User finalPatient = userRepository.findByUsername("patient").orElseGet(() -> {
                User patient = new User();
                patient.setUsername("patient");
                patient.setPassword(passwordEncoder.encode("patient123"));
                HashSet<Role> roles = new HashSet<>();
                roles.add(patientRole);
                patient.setRoles(roles);
                patient.setEnabled(true);
                return userRepository.save(patient);
            });
            
            if (patientRepository.findAll().stream().noneMatch(p -> p.getUser() != null && "patient".equals(p.getUser().getUsername()))) {
                Patient patientProfile = new Patient();
                patientProfile.setUser(finalPatient);
                patientProfile.setFirstName("John");
                patientProfile.setLastName("Smith");
                patientProfile.setDateOfBirth(LocalDate.of(1990, 1, 1));
                patientProfile.setEmail("patient@hospital.com");
                patientProfile.setPhone("0987654321");
                patientProfile.setMedicalHistory("None");
                patientRepository.save(patientProfile);
            }

            if (userRepository.findByUsername("staff").isEmpty()) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                HashSet<Role> roles = new HashSet<>();
                roles.add(staffRole);
                staff.setRoles(roles);
                staff.setEnabled(true);
                userRepository.save(staff);
            }
        };
    }
}
