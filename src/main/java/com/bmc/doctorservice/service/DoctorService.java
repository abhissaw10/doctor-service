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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.Cacheable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public void uploadDocuments(String doctorId, MultipartFile file) {
        try {
            s3Repository.uploadFileToS3(doctorId,file);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    public Doctor register(Doctor doctor) throws InvalidInputException {
        ValidationUtils.validate(doctor);
        doctor.setId(UUID.randomUUID().toString());
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
            return Optional
                .ofNullable(doctorRepository.findById(id)).get()
                .orElseThrow(RequestedResourceUnAvailableException::new);
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
                    if(o1.getRating() == null){
                        o1.setRating(0.0);
                    }
                    if(o2.getRating() == null){
                        o2.setRating(0.0);
                    }
                    return o1.getRating().compareTo(o2.getRating());
                }
            })
            .limit(20)
            .collect(Collectors.toList());
    }

    private void notify(Doctor doctor){
        notificationService.notifyDoctorRegistration(doctor);
    }

    public ByteArrayOutputStream downloadDocuments(String id, String documentId) throws IOException {
        return s3Repository.downloadFileFromS3(id,documentId);
    }

    public List<String> downloadDocumentMetadata(String doctorId) {
        return s3Repository.getDocumentsMetadata(doctorId);
    }
}
