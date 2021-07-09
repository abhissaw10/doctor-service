package com.bmc.doctorservice.controller.advice;

import com.bmc.doctorservice.exception.InvalidInputException;
import com.bmc.doctorservice.exception.RequestedResourceUnAvailableException;
import com.bmc.doctorservice.model.ErrorModel;
import com.bmc.doctorservice.util.DoctorConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.bmc.doctorservice.util.DoctorConstants.*;

@ControllerAdvice
public class DoctorControllerAdvice {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorModel> handleInvalidInput(InvalidInputException e){
        return new ResponseEntity(ErrorModel
            .builder()
            .errorCode(INVALID_INPUT_ERROR_CODE)
            .errorMessage(INVALID_INPUT_ERROR_MSG)
            .errorFields(e.getAttributeNames())
            .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RequestedResourceUnAvailableException.class)
    public ResponseEntity<ErrorModel> handleResourceUnavailableException(){
        return new ResponseEntity(ErrorModel
            .builder()
            .errorCode(RESOURCE_UNAVAILABLE)
            .errorMessage(RESOURCE_UNAVAILABLE_MSG)
            .build(), HttpStatus.NOT_FOUND);
    }
}
