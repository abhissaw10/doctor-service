package com.bmc.doctorservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateDoctorRequest {
    private String approvedBy;
    private String approverComments;
}
