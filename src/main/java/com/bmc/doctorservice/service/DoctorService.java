package com.bmc.doctorservice.service;

import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.model.UpdateDoctorRequest;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDate;
import java.util.List;

import static com.bmc.doctorservice.JavaUtils.randomLong;
import static com.bmc.doctorservice.model.DoctorStatus.*;

@Service
public class DoctorService {
    DoctorRepository doctorRepository;
    NotificationService notificationService;

    public DoctorService(DoctorRepository doctorRepository, NotificationService notificationService) {
        this.doctorRepository = doctorRepository;
        this.notificationService = notificationService;
    }

    public Doctor register(Doctor doctor){
        doctor.setId(randomLong());
        doctor.setStatus(PENDING.value);
        doctor.setRegistrationDate(LocalDate.now().toString());//TODO change to LocalDate
        doctorRepository.save(doctor);
        notificationService.notifyDoctorRegistration(doctor);
        return doctor;
    }

    public Doctor getDoctor(String id) {
        return doctorRepository.findById(id).get();
    }

    private Doctor updateStatus(String id, String status, UpdateDoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id).get();
        doctor.setStatus(status);
        doctor.setApprovedBy(request.getApprovedBy());
        doctor.setApproverComments(request.getApproverComments());
        doctor.setVerificationDate(LocalDate.now().toString());//TODO change it to LocalDate
        return doctorRepository.save(doctor);
    }

    public Doctor approve(String id, UpdateDoctorRequest request) {
        return updateStatus(id,APPROVED.value,request);
    }

    public Doctor reject(String id, UpdateDoctorRequest request) {
        return updateStatus(id, REJECTED.value,request);
    }

    public List<Doctor> getAllDoctorsWithFilters(String status, String speciality) {
        if(speciality!=null && !speciality.isEmpty()){
            return doctorRepository.findByStatusAndSpeciality(status,speciality);
        }
        return doctorRepository.findByStatus(status);
    }
}
