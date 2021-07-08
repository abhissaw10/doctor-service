package com.bmc.doctorservice.service;

import com.bmc.doctorservice.exception.InvalidInputException;
import com.bmc.doctorservice.exception.RequestedResourceUnAvailableException;
import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.model.Speciality;
import com.bmc.doctorservice.model.UpdateDoctorRequest;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.bmc.doctorservice.repository.S3Repository;
import com.bmc.doctorservice.util.ValidationUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bmc.doctorservice.JavaUtils.randomLong;
import static com.bmc.doctorservice.model.DoctorStatus.*;

@Log4j2
@Service
public class DoctorService {
    private DoctorRepository doctorRepository;
    private NotificationService notificationService;
    private S3Repository s3Repository;

    public DoctorService(DoctorRepository doctorRepository, NotificationService notificationService, S3Repository s3Repository) {
        this.doctorRepository = doctorRepository;
        this.notificationService = notificationService;
        this.s3Repository = s3Repository;
    }

    public void uploadDocuments(MultipartFile file) {
        try {
            s3Repository.uploadFileToS3(file);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Doctor register(Doctor doctor) throws InvalidInputException {
        ValidationUtils.validate(doctor);
        doctor.setId(randomLong());
        doctor.setStatus(PENDING.value);
        if(doctor.getSpeciality()==null){
            doctor.setSpeciality(Speciality.GENERAL_PHYSICIAN.name());
        }
        doctor.setRegistrationDate(LocalDate.now().toString());//TODO change to LocalDate
        doctorRepository.save(doctor);
        notify(doctor);
        return doctor;
    }

    public Doctor getDoctor(String id) {
            return Optional.ofNullable(doctorRepository.findById(id)).get().orElseThrow(RequestedResourceUnAvailableException::new);
    }

    private Doctor updateStatus(String id, String status, UpdateDoctorRequest request) {
        Doctor doctor = getDoctor(id);
        doctor.setStatus(status);
        doctor.setApprovedBy(request.getApprovedBy());
        doctor.setApproverComments(request.getApproverComments());
        doctor.setVerificationDate(LocalDate.now().toString());//TODO change it to LocalDate
        return doctorRepository.save(doctor);
    }

    public Doctor approve(String id, UpdateDoctorRequest request) {
        Doctor doctor = updateStatus(id,APPROVED.value,request);
        notify(doctor);
        return doctor;
    }

    public Doctor reject(String id, UpdateDoctorRequest request) {
        Doctor doctor = updateStatus(id, REJECTED.value,request);
        notify(doctor);
        return doctor;
    }

    public List<Doctor> getAllDoctorsWithFilters(String status, String speciality) {

        if(speciality!=null && !speciality.isEmpty()){
            return doctorRepository.findByStatusAndSpeciality(status,speciality);
        }
        return getActiveDoctorsSortedByRating(status);
    }

    @Cacheable(value = "doctorListByRating")
    private List<Doctor> getActiveDoctorsSortedByRating(String status){
        log.info("Fetching doctor list from the database");
        return doctorRepository.findByStatus(status)
            .stream()
            .sorted(new Comparator<Doctor>() {
                @Override
                public int compare(Doctor o1, Doctor o2) {
                    return o1.getRating().compareTo(o2.getRating());
                }
            })
            .limit(20)
            .collect(Collectors.toList());
    }

    private void notify(Doctor doctor){
        notificationService.notifyDoctorRegistration(doctor);
    }

    public ByteArrayOutputStream downloadDocuments(String doctorId) throws IOException {
        return s3Repository.downloadFileFromS3(doctorId);
    }
}
