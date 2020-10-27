package com.kwgdev.vineyard.utility;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * created by kw on 10/27/2020 @ 5:20 AM
 */

// AmazonS3 is a class from amazon dependency.
//        All other fields are just a representation of variables from our application.yml file.
//        The @Value annotation will bind application properties directly to class fields during application initialization.
//
//        We added method initializeAmazon() to set amazon credentials to amazon client.
//        Annotation @PostConstruct is needed to run this method after constructor will be called,
//        because class fields marked with @Value annotation is null in the constructor.

@Service
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;    @Value("${amazonProperties.bucketName}")
    private String bucketName;    @Value("${amazonProperties.accessKey}")
    private String accessKey;    @Value("${amazonProperties.secretKey}")
    private String secretKey;@PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
    }

    // helper methods

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl + ".png";
        s3client.deleteObject(new DeleteObjectRequest(bucketName + "/images/posts/", fileName));
        return "Successfully deleted";
    }

    public String uploadFile(MultipartFile multipartFile, String postImageName) {

//        String fileName = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
//            String fileName = generateFileName(multipartFile);
            postImageName = endpointUrl + "/" + bucketName + "/images/posts/" + postImageName + ".png";
            uploadFileTos3bucket(postImageName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred. Photo not saved!";
        }

//        System.out.println("Photo saved successfully!");
        return "Photo saved successfully!";
    }
}
