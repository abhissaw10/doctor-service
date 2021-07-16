package com.bmc.doctorservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Doctor {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String speciality;
    //TODO update the data type of dob
    private String dob;
    private Address address;
    private String mobile;
    private String emailId;
    //TODO not to be stored in free text
    private String pan;
    private String highestQualification;
    private List<Hospitals> hospitalsWorkedFor;

    private String college;
    private Integer totalYearsOfExp;
    private String status;
    private String approvedBy;
    private String approverComments;
    private String registrationDate;
    private String verificationDate;
    private Double rating;

}
