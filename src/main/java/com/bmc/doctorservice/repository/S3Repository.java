package com.bmc.doctorservice.repository;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
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
            "AKIASNBJ5FOSLTHQSO6L",
            "CJRKs7jUjcMlxlWMR/3/ZIobAEJJWZHZcPOGgbLc"
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
        if(!s3Client.doesBucketExistV2("bmc.doctor.documents")){
            s3Client.createBucket("bmc.doctor.documents");
        }
        s3Client.putObject("bmc.doctor.documents", "my-key", file.getInputStream(), metadata);
        return true;
    }

    public ByteArrayOutputStream downloadFileFromS3(String userId) throws IOException {
        S3Object s3Object = s3Client.getObject("bmc.doctor.documents","my-key");
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeBytes(inputStream.readAllBytes());

        return outputStream;
    }
}
