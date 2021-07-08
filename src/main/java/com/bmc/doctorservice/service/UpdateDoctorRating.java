package com.bmc.doctorservice.service;

import com.bmc.doctorservice.model.AverageRating;
import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UpdateDoctorRating {
    private final ObjectMapper mapper;
    private final DoctorRepository doctorRepository;

    @KafkaListener(topics = "${doctor.rating}", groupId = "${kafka.consumer.rating.groupid}",  containerFactory="avgRatingKafkaListenerContainerFactory")
    public void updateDoctorRating(Message<String> message) throws JsonProcessingException {
        AverageRating avgRating = mapper.readValue(message.getPayload(), AverageRating.class);
        Doctor doctor = doctorRepository.findById(avgRating.getDoctorId()).get();
        doctor.setRating(avgRating.getAvgRating());
        doctorRepository.save(doctor);
        System.out.println("Received Message in rating group: " + message.getPayload());
    }
}
