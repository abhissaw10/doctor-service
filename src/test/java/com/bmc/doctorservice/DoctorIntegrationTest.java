package com.bmc.doctorservice;

import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.bmc.doctorservice.service.DoctorService;
import com.bmc.doctorservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DoctorIntegrationTest {

    DoctorService doctorService;

    @Autowired
    DoctorRepository doctorRepository;

    @Autowired
    NotificationService notificationService;

    @BeforeEach
    public void setup(){
        doctorService = new DoctorService(doctorRepository,notificationService);
    }

    @Test
    public void doctorRegistrationPersisted() throws Exception{
        doctorService.register(givenDoctor());
        assertThat(doctorRepository.findAll())
            .hasSize(1)
            .isEqualTo(List.of(Doctor
                .builder()
                .doctorName("Abhishek")
                .dob("26-09-1983")
                .id("1")
                .status("Pending")
                .build()));
    }

    @Test
    public void doctorIdGeneratedWhenRegistrationPersisted() throws Exception{
        Doctor doc1 = doctorService.register(givenDoctor());
        Doctor doc2 = doctorService.register(givenDoctor());
        assertThat(doctorRepository.findAll())
            .hasSize(2);
        assertThat(doc1.getId()).isNotEqualTo(doc2.getId());
    }

    private Doctor givenDoctor(){
        return Doctor
            .builder()
            .doctorName("Abhishek")
            .dob("26-09-1983")
            .build();
    }
}
