package com.bmc.doctorservice.service;

import com.bmc.doctorservice.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.bmc.doctorservice.model.DoctorStatus.PENDING;

@Service
public class NotificationService {

    @Autowired
    SeSEmailVerification emailVerification;

    @Autowired
    KafkaTemplate<String, Doctor> kafkaTemplate;

    @Value("${doctor.registration.notification}")
    private String doctorRegistrationNotificationTopic;

    public void notifyDoctorRegistration(Doctor doctor){
        if(PENDING.value.equals(doctor.getStatus())) {
            emailVerification.sendVerificationEmail(doctor.getEmailId());
        }
        kafkaTemplate.send(doctorRegistrationNotificationTopic,doctor);
    }
}
