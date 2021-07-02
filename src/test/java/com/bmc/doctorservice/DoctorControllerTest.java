package com.bmc.doctorservice;

import com.bmc.doctorservice.controller.DoctorController;
import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.model.UpdateDoctorRequest;
import com.bmc.doctorservice.service.DoctorService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DoctorService doctorService;

    @Nested
    class RegisterDoctor{

        @Test
        void returns200WhenSuccessful() throws Exception {
            when(doctorService.register(any())).thenReturn(Doctor
                .builder()
                .id("1")
                .firstName("Abhishek")
                .dob(LocalDate.of(1983,9,26).toString())
                .status("Pending")
                .build());

            mockMvc.perform(post("/doctors")
                .contentType("application/json")
                .content("{\n" +
                    "  \"firstName\": \"Abhishek\",\n" +
                    "  \"dob\": \"26-09-1983\"\n" +
                    "}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                    "  \"id\": \"1\",\n" +
                    "  \"firstName\": \"Abhishek\",\n" +
                    "  \"dob\": \"26-09-1983\",\n" +
                    "  \"status\": \"Pending\"\n" +
                    "}"));
        }

    }

    @Nested
    class GetDoctor{

        @Test
        public void returnsDoctorIfExists() throws Exception {
            when(doctorService.getDoctor(any())).thenReturn(Doctor
                .builder()
                .id("1")
                .firstName("Abhishek")
                .dob(LocalDate.of(1983,9,26).toString())
                .status("Pending")
                .build());
            mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\n" +
                    "  \"firstName\": \"Abhishek\",\n" +
                    "  \"dob\": \"26-09-1983\"\n" +
                    "}",false));
        }
    }

    @Nested
    class UpdateDoctorStatus{

        @Test
        void statusUpdatedAsActiveWhenSuccessful() throws Exception {
            when(doctorService.approve("1", UpdateDoctorRequest.builder().build())).thenReturn(Doctor.builder().build());
                mockMvc.perform(put("/doctors/1/approve")
                .contentType("application/json")
                .content("{\n" +
                    "  \"approverComments\": \"Documents Verified\",\n" +
                    "  \"approver\": \"Manish\",\n" +
                    "  \"verificationDate\": \"22-05-2021\" \n" +
                    "}"))
                .andExpect(status().isOk())
                .andExpect(content().json(""));
        }

    }
}
