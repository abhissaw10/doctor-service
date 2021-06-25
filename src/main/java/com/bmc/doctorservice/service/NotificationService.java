package com.bmc.doctorservice.service;

import com.bmc.doctorservice.model.Doctor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    KafkaTemplate<String, Doctor> kafkaTemplate;

    @Value("${doctor.registration.notification}")
    private String doctorRegistrationNotificationTopic;

    public void notifyDoctorRegistration(Doctor doctor){
        kafkaTemplate.send(doctorRegistrationNotificationTopic,doctor);
    }
}
