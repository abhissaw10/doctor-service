package com.bmc.doctorservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Doctor {

    @Id
    private String id;
    private String doctorName;
    private String speciality;
    //TODO update the data type of dob
    private String dob;
    private String status;
    private String approvedBy;
    private String approverComments;
    private String registrationDate;
    private String verificationDate;
}
