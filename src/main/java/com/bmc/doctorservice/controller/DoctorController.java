package com.bmc.doctorservice.controller;

import com.bmc.doctorservice.exception.InvalidInputException;
import com.bmc.doctorservice.model.Doctor;
import com.bmc.doctorservice.model.UpdateDoctorRequest;
import com.bmc.doctorservice.service.DoctorService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class DoctorController {

    private DoctorService service;

    DoctorController(DoctorService service){
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/doctors/{id}")
    public ResponseEntity<Doctor> getDoctorDetails(@PathVariable String id){
        return ResponseEntity.ok(service.getDoctor(id));
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors(@RequestParam(value = "status",required = true) String status, @RequestParam(value = "speciality", required = false) String speciality){
        return ResponseEntity.ok(service.getAllDoctorsWithFilters(status,speciality));
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/doctors")
    public ResponseEntity<Doctor> registerDoctor(@RequestBody Doctor doctor) throws InvalidInputException {
        return ResponseEntity.ok(service.register(doctor));
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/doctors/{id}/documents")
    public ResponseEntity<String> uploadDocuments(@RequestParam("files") MultipartFile[] files, @PathVariable("id") String doctorId) throws IOException {
       int index=0;
        for(MultipartFile file: files){
            String name = file.getName();
            service.uploadDocuments(doctorId,file);
        }
        return ResponseEntity.ok("File(s) uploaded Successfully");
    }
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/doctors/{doctorId}/documents/{documentId}")
    public ResponseEntity<byte[]> downloadDocuments(@PathVariable String doctorId, @PathVariable("documentId") String documentId) throws IOException {
        String contentType = "application/octet-stream";
        ByteArrayOutputStream downloadStream = service.downloadDocuments(doctorId,documentId);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doctorId + "\"")
            .body(downloadStream.toByteArray());
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/doctors/{doctorId}/documents/metadata")
    public ResponseEntity<List<String>> downloadDocumentMetadata(@PathVariable String doctorId) throws IOException {
        return ResponseEntity.ok(service.downloadDocumentMetadata(doctorId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/doctors/{id}/approve")
    public ResponseEntity<Doctor> approveDoctor(@PathVariable String id, @RequestBody UpdateDoctorRequest request){
        return ResponseEntity.ok(service.approve(id,request));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/doctors/{id}/reject")
    public ResponseEntity<Doctor> rejectDoctor(@PathVariable String id, @RequestBody UpdateDoctorRequest request){
        return ResponseEntity.ok(service.reject(id,request));
    }
}
