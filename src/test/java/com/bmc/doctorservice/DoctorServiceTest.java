package com.bmc.doctorservice;

import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.repository.DoctorRepository;
import com.bmc.doctorservice.repository.S3Repository;
import com.bmc.doctorservice.service.DoctorService;
import com.bmc.doctorservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    @Mock
    S3Repository s3Repository;

    private DoctorService doctorService;

    @BeforeEach
    void setup(){
        doctorService = new DoctorService(doctorRepository,notificationService,s3Repository);
    }

    @Test
    public void registerDoctor() throws Exception{
        when(doctorRepository.save(any())).thenReturn(Doctor
            .builder()
            .firstName("Abhishek")
            .id("1")
            .dob(LocalDate.of(1983,9,26).toString())
            .status("Pending")
            .build());
        Doctor doctor = doctorService.register(givenDoctor());
        assertThat(doctor)
            .isNotNull()
            .isEqualTo(Doctor
                .builder()
                .id("1")
                .firstName("Abhishek")
                .dob(LocalDate.of(1983,9,26).toString())
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
            .firstName("Abhishek")
            .dob(LocalDate.of(1983,9,26).toString())
            .build();
    }
}
