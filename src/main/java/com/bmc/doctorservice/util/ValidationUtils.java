package com.bmc.doctorservice.util;

import com.bmc.doctorservice.exception.InvalidInputException;
import com.bmc.doctorservice.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ValidationUtils {

    public static void validate(Doctor doctor) throws InvalidInputException {
        List<String> errorFields = new ArrayList<>();
        if(doctor.getMobile()==null || !doctor.getMobile().matches("^\\d{10}$")){
            errorFields.add("Mobile");
        }
        if(doctor.getFirstName() == null || !doctor.getFirstName().matches("^[a-zA-Z\\\\s]{1,10}$")){
            errorFields.add("First Name");
        }
        if(doctor.getLastName() == null || !doctor.getLastName().matches("^[a-zA-Z\\\\s]{1,10}$")){
            errorFields.add("Last Name");
        }
        if(doctor.getPan() == null || !doctor.getPan().matches("[A-Z]{5}[0-9]{4}[A-Z]{1}")){
            errorFields.add("PAN");
        }
        if(doctor.getEmailId() == null || !doctor.getEmailId().matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errorFields.add("Email Id");
        }
        if(errorFields.size()>0) throw new InvalidInputException(errorFields);
    }
}
