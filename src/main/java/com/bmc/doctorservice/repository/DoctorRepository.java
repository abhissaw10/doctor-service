package com.bmc.doctorservice.repository;

import com.bmc.doctorservice.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor,String> {
    List<Doctor> findByStatus(String status);
    List<Doctor> findByStatusAndSpeciality(String status, String speciality);
}
