package com.bmc.doctorservice;

import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.bmc.doctorservice.service.DoctorService;
import com.bmc.doctorservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    DoctorRepository doctorRepository;

    @Mock
    NotificationService notificationService;

    private DoctorService doctorService;

    @BeforeEach
    void setup(){
        doctorService = new DoctorService(doctorRepository,notificationService);
    }

    @Test
    public void registerDoctor() throws Exception{
        when(doctorRepository.save(any())).thenReturn(Doctor
            .builder()
            .doctorName("Abhishek")
            .id("1")
            .dob("26-09-1983")
            .status("Pending")
            .build());
        Doctor doctor = doctorService.register(givenDoctor());
        assertThat(doctor)
            .isNotNull()
            .isEqualTo(Doctor
                .builder()
                .id("1")
                .doctorName("Abhishek")
                .dob("26-09-1983")
                .status("Pending")
                .build());
        verify(doctorRepository).save(any());
    }

    @Test
    public void getDoctorWhenIdExists(){
        when(doctorRepository.findById(any())).thenReturn(Optional.of(Doctor.builder().id("1").build()));
        Doctor doctor = doctorService.getDoctor("1");
        assertThat(doctor.getId()).isEqualTo("1");
    }

    private Doctor givenDoctor(){
        return  Doctor
            .builder()
            .doctorName("Abhishek")
            .dob("26-09-1983")
            .build();
    }
}
