package com.bmc.doctorservice.controller;

import com.bmc.doctorservice.exception.InvalidInputException;
import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.model.UpdateDoctorRequest;
import com.bmc.doctorservice.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
public class DoctorController {

    private DoctorService service;

    DoctorController(DoctorService service){
        this.service = service;
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<Doctor> getDoctorDetails(@PathVariable String id){
        return ResponseEntity.ok(service.getDoctor(id));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors(@RequestParam(value = "status",required = true) String status, @RequestParam(value = "speciality", required = false) String speciality){
        return ResponseEntity.ok(service.getAllDoctorsWithFilters(status,speciality));
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> registerDoctor(@RequestBody Doctor doctor) throws InvalidInputException {
        return ResponseEntity.ok(service.register(doctor));
    }

    @PostMapping("/doctors/{id}/documents")
    public ResponseEntity<String> uploadDocuments(@RequestParam("files") MultipartFile[] files) throws IOException {
        Arrays.asList(files).stream().forEach(file -> {
            service.uploadDocuments(file);
            //fileNames.add(file.getOriginalFilename());
        });
        return ResponseEntity.ok("Success");
    }

   /* @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = service.downloadDocument(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }*/

    @PutMapping("/doctors/{id}/approve")
    public ResponseEntity<Doctor> approveDoctor(@PathVariable String id, @RequestBody UpdateDoctorRequest request){
        return ResponseEntity.ok(service.approve(id,request));
    }

    @PutMapping("/doctors/{id}/reject")
    public ResponseEntity<Doctor> rejectDoctor(@PathVariable String id, @RequestBody UpdateDoctorRequest request){
        return ResponseEntity.ok(service.reject(id,request));
    }
}
