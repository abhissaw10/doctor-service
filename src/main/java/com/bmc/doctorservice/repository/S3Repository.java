package com.bmc.doctorservice.repository;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class S3Repository {

    private AmazonS3 s3Client;

    @Autowired
    ObjectMetadata metadata;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(
            "<AWS accesskey>",
            "<AWS secretkey>"
        );
        s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_2)
            .build();
    }

    public boolean uploadFileToS3(MultipartFile file) throws IOException {
        Map<String,String> metadataMap = new HashMap<>();
        metadata.addUserMetadata("fileName",file.getOriginalFilename());
        s3Client.putObject("my-bucket", "my-key", file.getInputStream(), metadata);
        return true;
    }
}
