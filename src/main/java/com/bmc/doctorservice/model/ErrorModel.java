package com.bmc.doctorservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorModel {
    private String errorCode;
    private String errorMessage;
    private List<String> errorFields;
}
