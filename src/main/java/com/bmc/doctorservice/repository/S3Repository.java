package com.bmc.doctorservice.repository;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class S3Repository {

    private AmazonS3 s3Client;

    @Value("${bmc.bucket.name}")
    private String bucketName;

    @Autowired
    ObjectMetadata metadata;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(
            "#########",
            "##############"
        );
        s3Client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_2)
            .build();
    }

    public boolean uploadFileToS3(String doctorId,MultipartFile file) throws IOException {
        Map<String,String> metadataMap = new HashMap<>();
        if(!s3Client.doesBucketExistV2(bucketName)){
            Bucket bucket = s3Client.createBucket(bucketName);

        }
        String subFolder = doctorId+"/"+file.getOriginalFilename();
        s3Client.putObject(bucketName, subFolder, file.getInputStream(), metadata);
        return true;
    }

    public ByteArrayOutputStream downloadFileFromS3(String doctorId, String documentId) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName,doctorId+"/"+documentId);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeBytes(inputStream.readAllBytes());
        return outputStream;
    }
    public List<String> getDocumentsMetadata(String doctorId){
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(doctorId).withMaxKeys(3);
        ListObjectsV2Result result;
        result = s3Client.listObjectsV2(req);
       return result.getObjectSummaries().stream().map(key->{
           return key.getKey().split("/")[1];
       }).collect(Collectors.toList());
    }
}
