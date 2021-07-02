package com.bmc.doctorservice.model;

import lombok.Builder;
import lombok.Data;

import java.time.YearMonth;

@Data
@Builder
public class Hospitals {
    private String hospitalName;
    private String hospitalBranch;
    private YearMonth employmentStart;
    private YearMonth employmentEnd;
}
